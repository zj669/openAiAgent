package com.zj.infrastructure.adapter.repository;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zj.domain.agent.adpater.repository.IAgentRepository;
import com.zj.domain.agent.model.vo.*;
import com.zj.domain.agent.model.vo.AiClientToolMcpVO.TransportConfigStdio.Stdio;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory.DynamicContext;
import com.zj.infrastructure.dao.IAiAgentDao;
import com.zj.infrastructure.dao.IAiAgentFlowConfigDao;
import com.zj.infrastructure.dao.IAiAgentTaskScheduleDao;
import com.zj.infrastructure.dao.IAiClientAdvisorDao;
import com.zj.infrastructure.dao.IAiClientApiDao;
import com.zj.infrastructure.dao.IAiClientConfigDao;
import com.zj.infrastructure.dao.IAiClientDao;
import com.zj.infrastructure.dao.IAiClientModelDao;
import com.zj.infrastructure.dao.IAiClientRagOrderDao;
import com.zj.infrastructure.dao.IAiClientSystemPromptDao;
import com.zj.infrastructure.dao.IAiClientToolMcpDao;
import com.zj.infrastructure.dao.po.*;
import com.zj.types.enums.AiAgentEnumVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class AgentRepository implements IAgentRepository {
    @Resource
    private IAiAgentDao aiAgentDao;

    @Resource
    private IAiAgentFlowConfigDao aiAgentFlowConfigDao;

    @Resource
    private IAiAgentTaskScheduleDao aiAgentTaskScheduleDao;

    @Resource
    private IAiClientAdvisorDao aiClientAdvisorDao;

    @Resource
    private IAiClientApiDao aiClientApiDao;

    @Resource
    private IAiClientConfigDao aiClientConfigDao;

    @Resource
    private IAiClientDao aiClientDao;

    @Resource
    private IAiClientModelDao aiClientModelDao;

    @Resource
    private IAiClientRagOrderDao aiClientRagOrderDao;

    @Resource
    private IAiClientSystemPromptDao aiClientSystemPromptDao;

    @Resource
    private IAiClientToolMcpDao aiClientToolMcpDao;

    @Override
    public void queryApiByClientIdS(List<String> commandIdList, DynamicContext context) {
        if (commandIdList == null || commandIdList.isEmpty()) {
            return ;
        }

        List<AiClientApiVO> result = new ArrayList<>();
        // 先查client对应的 api
        for(String commandId: commandIdList){
            List<AiClientConfig> configs = aiClientConfigDao.queryBySourceTypeAndId(AiAgentEnumVO.AI_CLIENT.getCode(), commandId);
            for (AiClientConfig config : configs) {
                if (AiAgentEnumVO.AI_CLIENT_MODEL.getCode().equals(config.getTargetType()) && config.getStatus() == 1) {
                    String modelId = config.getTargetId();

                    // 2. 通过modelId查询模型配置，获取apiId
                    AiClientModel model = aiClientModelDao.queryByModelId(modelId);
                    if (model != null && model.getStatus() == 1) {
                        String apiId = model.getApiId();

                        // 3. 通过apiId查询API配置信息
                        AiClientApi apiConfig = aiClientApiDao.queryByApiId(apiId);
                        if (apiConfig != null && apiConfig.getStatus() == 1) {
                            // 4. 转换为VO对象
                            AiClientApiVO apiVO = AiClientApiVO.builder()
                                    .apiId(apiConfig.getApiId())
                                    .baseUrl(apiConfig.getBaseUrl())
                                    .apiKey(apiConfig.getApiKey())
                                    .completionsPath(apiConfig.getCompletionsPath())
                                    .embeddingsPath(apiConfig.getEmbeddingsPath())
                                    .build();

                            // 避免重复添加相同的API配置
                            if (result.stream().noneMatch(vo -> vo.getApiId().equals(apiVO.getApiId()))) {
                                result.add(apiVO);
                            }
                        }
                    }
                }
            }

        }
        context.setValue(AiAgentEnumVO.AI_CLIENT_API.getDataName(), result);
    }

    @Override
    public void queryModelByClientIdS(List<String> commandIdList, DynamicContext context) {
        if (commandIdList == null || commandIdList.isEmpty()) {
            return;
        }

        List<AiClientModelVO> result = new ArrayList<>();

        for (String clientId : commandIdList) {
            // 1. 通过clientId查询关联的modelId
            List<AiClientConfig> configs = aiClientConfigDao.queryBySourceTypeAndId(AiAgentEnumVO.AI_CLIENT.getCode(), clientId);

            for (AiClientConfig config : configs) {
                if (AiAgentEnumVO.AI_CLIENT_MODEL.getCode().equals(config.getTargetType()) && config.getStatus() == 1) {
                    String modelId = config.getTargetId();

                    // 2. 通过modelId查询模型配置
                    AiClientModel model = aiClientModelDao.queryByModelId(modelId);
                    if (model != null && model.getStatus() == 1) {

                        // 3. 查询该模型关联的tool_mcp配置
                        List<AiClientConfig> toolMcpConfigs = aiClientConfigDao.queryBySourceTypeAndId(AiAgentEnumVO.AI_CLIENT_MODEL.getCode(), modelId);
                        List<String> toolMcpIds = new ArrayList<>();

                        for (AiClientConfig toolMcpConfig : toolMcpConfigs) {
                            if (AiAgentEnumVO.AI_CLIENT_TOOL_MCP.getCode().equals(toolMcpConfig.getTargetType()) && toolMcpConfig.getStatus() == 1) {
                                toolMcpIds.add(toolMcpConfig.getTargetId());
                            }
                        }

                        // 4. 转换为VO对象
                        AiClientModelVO modelVO = AiClientModelVO.builder()
                                .modelId(model.getModelId())
                                .apiId(model.getApiId())
                                .modelName(model.getModelName())
                                .modelType(model.getModelType())
                                .toolMcpIds(toolMcpIds)
                                .build();

                        // 避免重复添加相同的模型配置
                        if (result.stream().noneMatch(vo -> vo.getModelId().equals(modelVO.getModelId()))) {
                            result.add(modelVO);
                        }
                    }
                }
            }
        }
        context.setValue(AiAgentEnumVO.AI_CLIENT_MODEL.getDataName(), result);
    }

    @Override
    public void queryMcpByClientIdS(List<String> commandIdList, DynamicContext context) {
        if (commandIdList == null || commandIdList.isEmpty()) {
            return;
        }

        List<AiClientToolMcpVO> result = new ArrayList<>();
        Set<String> processedMcpIds = new HashSet<>();

        for (String clientId : commandIdList) {
            // 1. 通过clientId查询关联的model配置
            List<AiClientConfig> clientConfigs = aiClientConfigDao.queryBySourceTypeAndId(AiAgentEnumVO.AI_CLIENT.getCode(), clientId);

            for (AiClientConfig clientConfig : clientConfigs) {
                if (AiAgentEnumVO.AI_CLIENT_MODEL.getCode().equals(clientConfig.getTargetType()) && clientConfig.getStatus() == 1) {
                    String modelId = clientConfig.getTargetId();

                    // 2. 通过modelId查询关联的tool_mcp配置
                    List<AiClientConfig> modelConfigs = aiClientConfigDao.queryBySourceTypeAndId(AiAgentEnumVO.AI_CLIENT_MODEL.getCode(), modelId);

                    for (AiClientConfig modelConfig : modelConfigs) {
                        if (AiAgentEnumVO.AI_CLIENT_TOOL_MCP.getCode().equals(modelConfig.getTargetType()) && modelConfig.getStatus() == 1) {
                            String mcpId = modelConfig.getTargetId();

                            // 避免重复处理相同的mcpId
                            if (processedMcpIds.contains(mcpId)) {
                                continue;
                            }
                            processedMcpIds.add(mcpId);

                            // 3. 通过mcpId查询ai_client_tool_mcp表获取MCP工具配置
                            AiClientToolMcp toolMcp = aiClientToolMcpDao.queryByMcpId(mcpId);
                            if (toolMcp != null && toolMcp.getStatus() == 1) {
                                // 4. 转换为VO对象
                                AiClientToolMcpVO mcpVO = AiClientToolMcpVO.builder()
                                        .mcpId(toolMcp.getMcpId())
                                        .mcpName(toolMcp.getMcpName())
                                        .transportType(toolMcp.getTransportType())
                                        .transportConfig(toolMcp.getTransportConfig())
                                        .requestTimeout(toolMcp.getRequestTimeout())
                                        .build();

                                String transportConfig = toolMcp.getTransportConfig();
                                String transportType = toolMcp.getTransportType();

                                try {
                                    if ("sse".equals(transportType)) {
                                        // 解析SSE配置
                                        ObjectMapper objectMapper = new ObjectMapper();
                                        AiClientToolMcpVO.TransportConfigSse transportConfigSse = objectMapper.readValue(transportConfig, AiClientToolMcpVO.TransportConfigSse.class);
                                        mcpVO.setTransportConfigSse(transportConfigSse);
                                    } else if ("stdio".equals(transportType)) {
                                        // 解析STDIO配置
                                        Map<String, Stdio> stdio = JSON.parseObject(transportConfig,
                                                new TypeReference<>() {
                                                });

                                        AiClientToolMcpVO.TransportConfigStdio transportConfigStdio = new AiClientToolMcpVO.TransportConfigStdio();
                                        transportConfigStdio.setStdio(stdio);

                                        mcpVO.setTransportConfigStdio(transportConfigStdio);
                                    }
                                } catch (Exception e) {
                                    log.error("解析传输配置失败: {}", e.getMessage(), e);
                                }
                                result.add(mcpVO);
                            }
                        }
                    }
                }
            }
        }
        context.setValue(AiAgentEnumVO.AI_CLIENT_TOOL_MCP.getDataName(), result);
    }

    @Override
    public void queryAdvisorByClientIdS(List<String> commandIdList, DynamicContext context) {
        if (commandIdList == null || commandIdList.isEmpty()) {
            return ;
        }

        List<AiClientAdvisorVO> result = new ArrayList<>();
        Set<String> processedAdvisorIds = new HashSet<>();

        for (String clientId : commandIdList) {
            // 1. 查询客户端相关的advisor配置
            List<AiClientConfig> configs = aiClientConfigDao.queryBySourceTypeAndId("client", clientId);

            for (AiClientConfig config : configs) {
                if (config.getStatus() != 1 || !"advisor".equals(config.getTargetType())) {
                    continue;
                }

                String advisorId = config.getTargetId();
                if (processedAdvisorIds.contains(advisorId)) {
                    continue;
                }
                processedAdvisorIds.add(advisorId);

                // 2. 查询advisor详细信息
                AiClientAdvisor aiClientAdvisor = aiClientAdvisorDao.queryByAdvisorId(advisorId);
                if (aiClientAdvisor == null || aiClientAdvisor.getStatus() != 1) {
                    continue;
                }

                // 3. 解析extParam中的配置
                AiClientAdvisorVO.ChatMemory chatMemory = null;
                AiClientAdvisorVO.RagAnswer ragAnswer = null;

                String extParam = aiClientAdvisor.getExtParam();
                if (extParam != null && !extParam.trim().isEmpty()) {
                    try {
                        if ("ChatMemory".equals(aiClientAdvisor.getAdvisorType())) {
                            // 解析chatMemory配置
                            chatMemory = JSON.parseObject(extParam, AiClientAdvisorVO.ChatMemory.class);
                        } else if ("RagAnswer".equals(aiClientAdvisor.getAdvisorType())) {
                            // 解析ragAnswer配置
                            ragAnswer = JSON.parseObject(extParam, AiClientAdvisorVO.RagAnswer.class);
                        }
                    } catch (Exception e) {
                        // 解析失败时忽略，使用默认值null
                    }
                }

                // 4. 构建AiClientAdvisorVO对象
                AiClientAdvisorVO advisorVO = AiClientAdvisorVO.builder()
                        .advisorId(aiClientAdvisor.getAdvisorId())
                        .advisorName(aiClientAdvisor.getAdvisorName())
                        .advisorType(aiClientAdvisor.getAdvisorType())
                        .orderNum(aiClientAdvisor.getOrderNum())
                        .chatMemory(chatMemory)
                        .ragAnswer(ragAnswer)
                        .build();

                result.add(advisorVO);
            }
        }
        context.setValue(AiAgentEnumVO.AI_CLIENT_ADVISOR.getDataName(), result);
    }

    @Override
    public void queryPromptByClientIdS(List<String> commandIdList, DynamicContext context) {
        if (commandIdList == null || commandIdList.isEmpty()) {
            return ;
        }

        Map<String, AiClientSystemPromptVO> result = new HashMap<>();
        Set<String> processedPromptIds = new HashSet<>();

        for (String clientId : commandIdList) {
            // 1. 通过clientId查询关联的prompt配置
            List<AiClientConfig> configs = aiClientConfigDao.queryBySourceTypeAndId(AiAgentEnumVO.AI_CLIENT.getCode(), clientId);

            for (AiClientConfig config : configs) {
                if ("prompt".equals(config.getTargetType()) && config.getStatus() == 1) {
                    String promptId = config.getTargetId();

                    // 避免重复处理相同的promptId
                    if (processedPromptIds.contains(promptId)) {
                        continue;
                    }
                    processedPromptIds.add(promptId);

                    // 2. 通过promptId查询ai_client_system_prompt表获取系统提示词配置
                    AiClientSystemPrompt systemPrompt = aiClientSystemPromptDao.queryByPromptId(promptId);
                    if (systemPrompt != null && systemPrompt.getStatus() == 1) {
                        // 3. 转换为VO对象
                        AiClientSystemPromptVO promptVO = AiClientSystemPromptVO.builder()
                                .promptId(systemPrompt.getPromptId())
                                .promptName(systemPrompt.getPromptName())
                                .promptContent(systemPrompt.getPromptContent())
                                .description(systemPrompt.getDescription())
                                .build();

                        result.put(systemPrompt.getPromptId() ,promptVO);
                    }
                }
            }
        }
        context.setValue(AiAgentEnumVO.AI_CLIENT_SYSTEM_PROMPT.getDataName(), result);
    }

    @Override
    public void queryAiClientVOByClientIds(List<String> commandIdList, DynamicContext context) {
        if (commandIdList == null || commandIdList.isEmpty()) {
            return;
        }

        List<AiClientVO> result = new ArrayList<>();
        Set<String> processedClientIds = new HashSet<>();

        for (String clientId : commandIdList) {
            if (processedClientIds.contains(clientId)) {
                continue;
            }
            processedClientIds.add(clientId);

            // 1. 查询客户端基本信息
            AiClient aiClient = aiClientDao.queryByClientId(clientId);
            if (aiClient == null || aiClient.getStatus() != 1) {
                continue;
            }

            // 2. 查询客户端相关配置
            List<AiClientConfig> configs = aiClientConfigDao.queryBySourceTypeAndId("client", clientId);

            String modelId = null;
            List<String> promptIdList = new ArrayList<>();
            List<String> mcpIdList = new ArrayList<>();
            List<String> advisorIdList = new ArrayList<>();

            for (AiClientConfig config : configs) {
                if (config.getStatus() != 1) {
                    continue;
                }

                switch (config.getTargetType()) {
                    case "model":
                        modelId = config.getTargetId();
                        break;
                    case "prompt":
                        promptIdList.add(config.getTargetId());
                        break;
                    case "tool_mcp":
                        mcpIdList.add(config.getTargetId());
                        break;
                    case "advisor":
                        advisorIdList.add(config.getTargetId());
                        break;
                }
            }

            // 3. 构建AiClientVO对象
            AiClientVO aiClientVO = AiClientVO.builder()
                    .clientId(aiClient.getClientId())
                    .clientName(aiClient.getClientName())
                    .description(aiClient.getDescription())
                    .modelId(modelId)
                    .promptIdList(promptIdList)
                    .mcpIdList(mcpIdList)
                    .advisorIdList(advisorIdList)
                    .build();

            result.add(aiClientVO);
        }
        context.setValue(AiAgentEnumVO.AI_CLIENT.getDataName(), result);

    }

    @Override
    public Map<String, AiAgentClientFlowConfigVO> queryAiAgentClientFlowConfig(String aiAgentId) {
        if (aiAgentId == null || aiAgentId.trim().isEmpty()) {
            return Map.of();
        }
        try {
            // 根据智能体ID查询流程配置列表
            List<AiAgentFlowConfig> flowConfigs = aiAgentFlowConfigDao.queryByAgentId(aiAgentId);

            if (flowConfigs == null || flowConfigs.isEmpty()) {
                return Map.of();
            }

            // 转换为Map结构，key为clientId，value为AiAgentClientFlowConfigVO
            Map<String, AiAgentClientFlowConfigVO> result = new HashMap<>();

            for (AiAgentFlowConfig flowConfig : flowConfigs) {
                AiAgentClientFlowConfigVO configVO = AiAgentClientFlowConfigVO.builder()
                        .stepPrompt(flowConfig.getStepPrompt())
                        .clientId(flowConfig.getClientId())
                        .clientName(flowConfig.getClientName())
                        .clientType(flowConfig.getClientType())
                        .sequence(flowConfig.getSequence())
                        .build();

                result.put(flowConfig.getClientType(), configVO);
            }

            return result;
        } catch (NumberFormatException e) {
            log.error("Invalid aiAgentId format: {}", aiAgentId, e);
            return Map.of();
        } catch (Exception e) {
            log.error("Query ai agent client flow config failed, aiAgentId: {}", aiAgentId, e);
            return Map.of();
        }

    }

    @Override
    public List<String> queryClientIdsByAgentId(String aiAgentId) {
        List<AiAgentFlowConfig> aiAgentFlowConfigs = aiAgentFlowConfigDao.queryByAgentId(aiAgentId);
        return aiAgentFlowConfigs.stream().map(AiAgentFlowConfig::getClientId).toList();
    }
}
