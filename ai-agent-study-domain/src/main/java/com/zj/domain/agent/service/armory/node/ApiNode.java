package com.zj.domain.agent.service.armory.node;

import com.zj.domain.agent.model.entity.ArmoryCommandEntity;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory.DynamicContext;
import com.zj.types.common.design.tree.handler.StrategyHandler;
import org.springframework.stereotype.Component;

@Component
public class ApiNode extends AgentAromorSupport {
    @Override
    protected String doApply(ArmoryCommandEntity requestParams, DynamicContext context) {
        return "";
    }

    @Override
    public StrategyHandler<ArmoryCommandEntity, DynamicContext, String> getStrategyHandler(ArmoryCommandEntity requestParams, DynamicContext context) {
        return null;
    }
}
