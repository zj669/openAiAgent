package com.zj.domain.agent.service.armory.node;

import com.zj.domain.agent.model.entity.ArmoryCommandEntity;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory;
import com.zj.domain.agent.service.armory.model.AgentArmoryVO;
import com.zj.types.common.design.tree.handler.StrategyHandler;
import org.springframework.stereotype.Component;

@Component
public class EndNode extends AgentAromorSupport{
    @Override
    protected String beanName(String beanId) {
        return "";
    }

    @Override
    protected String dataName() {
        return "";
    }

    @Override
    protected AgentArmoryVO doApply(ArmoryCommandEntity requestParams, DefaultAgentArmoryFactory.DynamicContext context) {
        return AgentArmoryVO.builder()
                .dynamicContext( context)
                .build();
    }

    @Override
    public StrategyHandler<ArmoryCommandEntity, DefaultAgentArmoryFactory.DynamicContext, AgentArmoryVO> getStrategyHandler(ArmoryCommandEntity requestParams, DefaultAgentArmoryFactory.DynamicContext context) {
        return getDefaultStrategyHandler();
    }
}
