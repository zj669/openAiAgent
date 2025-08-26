package com.zj.types.common.design.tree;


import com.zj.types.common.design.tree.handler.StrategyHandler;

public interface StrategyMapper<T, D, R> {
    /**
     * 映射器
     * @param requestParams 请求参数
     * @param context 上下文参数
     * @return 策略处理器
     */
    StrategyHandler<T, D, R> getStrategyHandler(T requestParams, D context);
}
