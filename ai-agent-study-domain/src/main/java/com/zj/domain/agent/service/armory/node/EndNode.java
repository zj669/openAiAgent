package com.zj.domain.agent.service.armory.node;

import com.zj.domain.agent.model.entity.ArmoryCommandEntity;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory;
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
    protected String doApply(ArmoryCommandEntity requestParams, DefaultAgentArmoryFactory.DynamicContext context) {
        return "";
    }

    @Override
    public StrategyHandler<ArmoryCommandEntity, DefaultAgentArmoryFactory.DynamicContext, String> getStrategyHandler(ArmoryCommandEntity requestParams, DefaultAgentArmoryFactory.DynamicContext context) {
        return getDefaultStrategyHandler();
    }
}
