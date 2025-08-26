package com.zj.domain.agent.service.armory.node;

import com.zj.domain.agent.model.entity.ArmoryCommandEntity;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory.DynamicContext;
import com.zj.types.common.design.tree.AbsractMultiTreadStrategyRouter;
import com.zj.types.common.design.tree.handler.StrategyHandler;

public abstract class AgentAromorSupport extends AbsractMultiTreadStrategyRouter<ArmoryCommandEntity, DefaultAgentArmoryFactory.DynamicContext, String> {

    @Override
    protected void multiThread(ArmoryCommandEntity requestParams, DynamicContext context) {

    }

}
