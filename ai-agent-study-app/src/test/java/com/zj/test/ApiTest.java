package com.zj.test;

import com.alibaba.fastjson2.JSON;
import com.zj.domain.agent.model.entity.ArmoryCommandEntity;
import com.zj.domain.agent.model.entity.ExecuteCommandEntity;
import com.zj.domain.agent.model.vo.AiClientApiVO;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory.DynamicContext;
import com.zj.domain.agent.service.armory.model.AgentArmoryVO;
import com.zj.domain.agent.service.execute.AbstractExecuteStrategy;
import com.zj.domain.agent.service.execute.auto.factory.DefaultAutoAgentExecuteStrategyFactory;
import com.zj.domain.agent.service.execute.model.ModelExecuteStrategy;
import com.zj.infrastructure.dao.IAiClientApiDao;
import com.zj.infrastructure.dao.IAiClientToolMcpDao;
import com.zj.infrastructure.dao.po.AiClientApi;
import com.zj.infrastructure.dao.po.AiClientToolMcp;
import com.zj.types.common.design.tree.handler.StrategyHandler;
import com.zj.types.enums.AiAgentEnumVO;
import io.modelcontextprotocol.client.McpSyncClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {
    @Resource
    private IAiClientToolMcpDao aiClientToolMcpDao;
    @Resource
    private DefaultAgentArmoryFactory defaultAgentArmoryFactory;
    @Resource
    protected ApplicationContext applicationContext;
    @Resource
    private ModelExecuteStrategy modelExecuteStrategy;
    @Resource
    private IAiClientApiDao aiClientApiDao;
    @Resource(name = "webClientBuilder1")
    private WebClient.Builder webClientBuilder;
    @Resource(name = "restClientBuilder1")
    private RestClient.Builder restClientBuilder;
    @Resource
    private DefaultAutoAgentExecuteStrategyFactory defaultAutoAgentExecuteStrategyFactory;

    @Test
    public void test() {
        StrategyHandler<ArmoryCommandEntity, DynamicContext, AgentArmoryVO> armoryCommandEntityDynamicContextStringStrategyHandler = defaultAgentArmoryFactory.strategyHandler();
        ArmoryCommandEntity armoryCommandEntity = new ArmoryCommandEntity();
        armoryCommandEntity.setCommandType(AiAgentEnumVO.AI_CLIENT.getLoadDataStrategy());
        armoryCommandEntity.setCommandIdList(Arrays.asList("3001"));
        DynamicContext dynamicContext = new DynamicContext();
        armoryCommandEntityDynamicContextStringStrategyHandler.apply(armoryCommandEntity, dynamicContext);
        System.out.println(JSON.toJSONString(dynamicContext));
        List<AiClientApiVO> value = dynamicContext.getValue(AiAgentEnumVO.AI_CLIENT_API.getDataName());
        Object bean = applicationContext.getBean(AiAgentEnumVO.AI_CLIENT_API.getBeanName(value.get(0).getApiId()));
        System.out.println(JSON.toJSONString( bean));
    }

    @Test
    public void test2() {
//        System.out.println(modelExecuteStrategy.execute(new ExecuteCommandEntity("6", "你是谁")));
    }

    @Test
    public void test4() {
        StrategyHandler<ExecuteCommandEntity, AbstractExecuteStrategy.DynamicContext, String> executeCommandEntityDynamicContextStringStrategyHandler = defaultAutoAgentExecuteStrategyFactory.armoryStrategyHandler();
        AbstractExecuteStrategy.DynamicContext dynamicContext = new AbstractExecuteStrategy.DynamicContext();
        System.out.println(executeCommandEntityDynamicContextStringStrategyHandler.apply(ExecuteCommandEntity.builder()
                .userMessage("大象装进冰箱要几步")
                .aiAgentId("3")
                .maxStep(1)
                .sessionId("1")
                .build(), dynamicContext));

        System.out.println((String) dynamicContext.getValue("finalSummary"));

    }

}
