package com.zj.domain.agent.service.armory.strategy;

import com.zj.domain.agent.model.entity.ArmoryCommandEntity;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory.DynamicContext;

public interface ILoadDataStrategy {
    void loadData(ArmoryCommandEntity requestParams, DynamicContext context);
}
