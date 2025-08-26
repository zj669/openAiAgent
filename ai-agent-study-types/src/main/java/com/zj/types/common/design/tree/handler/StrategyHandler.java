package com.zj.types.common.design.tree.handler;

public interface StrategyHandler<T, D, R> {
    /**
     * 处理器
     * @param requestParams 请求参数
     * @param context 上下文
     * @return 返回值
     */
    R apply(T requestParams, D context);
}
