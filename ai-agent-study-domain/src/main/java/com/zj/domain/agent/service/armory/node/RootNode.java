package com.zj.domain.agent.service.armory.node;

import com.zj.domain.agent.model.entity.ArmoryCommandEntity;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory.DynamicContext;
import com.zj.domain.agent.service.armory.strategy.ILoadDataStrategy;
import com.zj.types.common.design.tree.handler.StrategyHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RootNode extends AgentAromorSupport{
    @Resource
    private ApiNode apiNode;
    @Resource
    private Map<String, ILoadDataStrategy> loadDataStrategyMap;

    @Override
    protected String doApply(ArmoryCommandEntity requestParams, DynamicContext context) {
        return route(requestParams, context);
    }

    @Override
    public StrategyHandler<ArmoryCommandEntity, DynamicContext, String> getStrategyHandler(ArmoryCommandEntity requestParams, DynamicContext context) {
        return apiNode;
    }

    @Override
    protected void multiThread(ArmoryCommandEntity requestParams, DynamicContext context) {
        // 策略加载数据
        String commandType = requestParams.getCommandType();
        ILoadDataStrategy loadDataStrategy = loadDataStrategyMap.get(commandType);
        loadDataStrategy.loadData(requestParams, context);
    }
}
