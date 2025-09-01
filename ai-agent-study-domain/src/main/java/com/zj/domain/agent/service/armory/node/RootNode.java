package com.zj.domain.agent.service.armory.node;

import com.zj.domain.agent.model.entity.ArmoryCommandEntity;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory.DynamicContext;
import com.zj.domain.agent.service.armory.model.AgentArmoryVO;
import com.zj.domain.agent.service.armory.strategy.ILoadDataStrategy;
import com.zj.types.common.design.tree.handler.StrategyHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class RootNode extends AgentAromorSupport{
    @Resource
    private ApiNode apiNode;
    @Resource
    private Map<String, ILoadDataStrategy> loadDataStrategyMap;

    @Override
    protected AgentArmoryVO doApply(ArmoryCommandEntity requestParams, DynamicContext context) {
        return route(requestParams, context);
    }

    @Override
    public StrategyHandler<ArmoryCommandEntity, DynamicContext, AgentArmoryVO> getStrategyHandler(ArmoryCommandEntity requestParams, DynamicContext context) {
        return apiNode;
    }

    @Override
    protected void multiThread(ArmoryCommandEntity requestParams, DynamicContext context) {
        // 策略加载数据
        String commandType = requestParams.getCommandType();
        log.info("开始加载数据 commandType{}", commandType);
        ILoadDataStrategy loadDataStrategy = loadDataStrategyMap.get(commandType);
        if (loadDataStrategy == null) {
            log.error("未找到对应的策略");
        }
        loadDataStrategy.loadData(requestParams, context);
    }

    @Override
    protected String beanName(String beanId) {
        return "";
    }

    @Override
    protected String dataName() {
        return "";
    }
}
