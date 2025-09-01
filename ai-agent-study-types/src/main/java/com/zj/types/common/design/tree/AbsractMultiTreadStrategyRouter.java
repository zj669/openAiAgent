package com.zj.types.common.design.tree;


import com.zj.types.common.design.tree.handler.DefaultStrategyHandler;
import com.zj.types.common.design.tree.handler.StrategyHandler;
import lombok.Data;

@Data
public abstract class AbsractMultiTreadStrategyRouter<T, D, R>  implements StrategyMapper<T, D, R>, StrategyHandler<T, D, R> {
    protected DefaultStrategyHandler<T, D, R> defaultStrategyHandler;
    public  R route(T t, D d){
        StrategyHandler<T, D, R> strategyHandler = getStrategyHandler(t, d);
        if(strategyHandler != null){
            return strategyHandler.apply(t, d);
        }
        return defaultStrategyHandler.apply(t, d);
    };

    @Override
    public R apply(T requestParams, D context) {
        multiThread(requestParams, context);
        return doApply(requestParams, context);
    }


    protected abstract R doApply(T requestParams, D context);

    /**
     * 异步加载数据
     * @param requestParams
     * @param context
     */
    protected abstract void multiThread(T requestParams, D context);

}