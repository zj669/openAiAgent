package com.zj.types.common.design.link.simpleChain;

import org.springframework.stereotype.Service;

@Service

public abstract class AbstracSimpleChainModel<T, D, R> {
    protected AbstracSimpleChainModel<T, D, R> nextModel;

    public R handle(T t, D d) {
        R result = apply(t,d);
        if(next() != null){
            return next().handle(t, d);
        }
        return result;
    }

    protected abstract R apply(T t, D d);

    protected AbstracSimpleChainModel<T, D, R> next(){
        return nextModel;
    };

    public void setNextModel(AbstracSimpleChainModel<T, D, R> nextModel){
        this.nextModel = nextModel;
    }
}
