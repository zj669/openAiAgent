package com.zj.domain.agent.service.armory.node;

import com.zj.domain.agent.model.entity.ArmoryCommandEntity;
import com.zj.domain.agent.model.vo.AiClientModelVO;
import com.zj.domain.agent.model.vo.AiClientToolMcpVO;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory;
import com.zj.types.common.design.tree.handler.StrategyHandler;
import com.zj.types.enums.AiAgentEnumVO;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class McpNode extends AgentAromorSupport {
    @Resource
    private AdvisorNode advisorNode;
    @Override
    protected String beanName(String beanId) {
        return AiAgentEnumVO.AI_CLIENT_TOOL_MCP.getBeanName(beanId);
    }

    @Override
    protected String dataName() {
        return AiAgentEnumVO.AI_CLIENT_TOOL_MCP.getDataName();
    }

    @Override
    protected String doApply(ArmoryCommandEntity requestParams, DefaultAgentArmoryFactory.DynamicContext context) {
        log.info("Ai Agent 构建节点，客户端,McpNode");
        List<AiClientToolMcpVO> value = context.getValue(dataName());
        for (AiClientToolMcpVO mcpVO : value) {
            // 创建 MCP 服务
            McpSyncClient mcpSyncClient = createMcpSyncClient(mcpVO);

            // 注册 MCP 对象
            registerBean(beanName(mcpVO.getMcpId()), McpSyncClient.class, mcpSyncClient);
        }
        return route(requestParams, context);
    }

    @Override
    public StrategyHandler<ArmoryCommandEntity, DefaultAgentArmoryFactory.DynamicContext, String> getStrategyHandler(ArmoryCommandEntity requestParams, DefaultAgentArmoryFactory.DynamicContext context) {
        return advisorNode;
    }

private McpSyncClient createMcpSyncClient(AiClientToolMcpVO aiClientToolMcpVO) {
    String transportType = aiClientToolMcpVO.getTransportType();

    switch (transportType) {
        case "sse" -> {
            AiClientToolMcpVO.TransportConfigSse transportConfigSse = aiClientToolMcpVO.getTransportConfigSse();
            // http://127.0.0.1:9999/sse?apikey=DElk89iu8Ehhnbu
            String originalBaseUri = transportConfigSse.getBaseUri();
            String baseUri;
            String sseEndpoint;

            int queryParamStartIndex = originalBaseUri.indexOf("sse");
            if (queryParamStartIndex != -1) {
                baseUri = originalBaseUri.substring(0, queryParamStartIndex - 1);
                sseEndpoint = originalBaseUri.substring(queryParamStartIndex - 1);
            } else {
                baseUri = originalBaseUri;
                sseEndpoint = transportConfigSse.getSseEndpoint();
            }

            sseEndpoint = StringUtils.isBlank(sseEndpoint) ? "/sse" : sseEndpoint;

            HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport
                    .builder(baseUri) // 使用截取后的 baseUri
                    .sseEndpoint(sseEndpoint) // 使用截取或默认的 sseEndpoint
                    .build();

            McpSyncClient mcpSyncClient = McpClient.sync(sseClientTransport).requestTimeout(Duration.ofMinutes(aiClientToolMcpVO.getRequestTimeout())).build();
            var init_sse = mcpSyncClient.initialize();

            log.info("Tool SSE MCP Initialized {}", init_sse);
            return mcpSyncClient;
        }
        case "stdio" -> {
            AiClientToolMcpVO.TransportConfigStdio transportConfigStdio = aiClientToolMcpVO.getTransportConfigStdio();
            Map<String, AiClientToolMcpVO.TransportConfigStdio.Stdio> stdioMap = transportConfigStdio.getStdio();
            AiClientToolMcpVO.TransportConfigStdio.Stdio stdio = stdioMap.get(aiClientToolMcpVO.getMcpName());

            // https://github.com/modelcontextprotocol/servers/tree/main/src/filesystem
            var stdioParams = ServerParameters.builder(stdio.getCommand())
                    .args(stdio.getArgs())
                    .env(stdio.getEnv())
                    .build();

            var mcpClient = McpClient.sync(new StdioClientTransport(stdioParams))
                    .requestTimeout(Duration.ofSeconds(aiClientToolMcpVO.getRequestTimeout())).build();
            var init_stdio = mcpClient.initialize();

            log.info("Tool Stdio MCP Initialized {}", init_stdio);
            return mcpClient;
        }
    }

    throw new RuntimeException("err! transportType " + transportType + " not exist!");
}
}
