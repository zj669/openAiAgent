package com.zj.domain.agent.service.execute.auto.factory;

import com.zj.domain.agent.model.entity.ExecuteCommandEntity;
import com.zj.domain.agent.service.execute.AbstractExecuteStrategy;
import com.zj.domain.agent.service.execute.auto.step.RootNode;
import com.zj.types.common.design.tree.handler.StrategyHandler;
import org.springframework.stereotype.Service;


/**
 * 工厂类
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/7/27 16:34
 */
@Service
public class DefaultAutoAgentExecuteStrategyFactory {

    private final RootNode executeRootNode;

    public DefaultAutoAgentExecuteStrategyFactory(RootNode executeRootNode) {
        this.executeRootNode = executeRootNode;
    }

    public StrategyHandler<ExecuteCommandEntity, AbstractExecuteStrategy.DynamicContext, String> armoryStrategyHandler(){
        return executeRootNode;
    }

}
