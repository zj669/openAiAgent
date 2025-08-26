package com.zj.domain.agent.service.armory.node;

import com.zj.domain.agent.model.entity.ArmoryCommandEntity;
import com.zj.domain.agent.model.vo.AiClientApiVO;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory.DynamicContext;
import com.zj.types.common.design.tree.handler.StrategyHandler;
import com.zj.types.enums.AiAgentEnumVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ApiNode extends AgentAromorSupport {
    @Resource
    private ModelNode modelNode;
    @Override
    protected String doApply(ArmoryCommandEntity requestParams, DynamicContext context) {
        log.info("Ai Agent 构建节点，客户端,ApiNode");
        List<AiClientApiVO> value = context.getValue(dataName());
        for (AiClientApiVO aiClientApiVO : value) {
            OpenAiApi build = OpenAiApi.builder()
                    .baseUrl(aiClientApiVO.getBaseUrl())
                    .apiKey(aiClientApiVO.getApiKey())
                    .build();
            registerBean(beanName(aiClientApiVO.getApiId()), OpenAiApi.class,  build);
        }
        return route(requestParams, context);
    }

    @Override
    public StrategyHandler<ArmoryCommandEntity, DynamicContext, String> getStrategyHandler(ArmoryCommandEntity requestParams, DynamicContext context) {
        return modelNode;
    }

    @Override
    protected String beanName(String beanId) {
        return AiAgentEnumVO.AI_CLIENT_API.getBeanName(beanId);
    }

    @Override
    protected String dataName() {
        return AiAgentEnumVO.AI_CLIENT_API.getDataName();
    }
}
