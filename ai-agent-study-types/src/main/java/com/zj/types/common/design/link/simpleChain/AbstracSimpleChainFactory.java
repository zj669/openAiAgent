package com.zj.types.common.design.link.simpleChain;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public abstract class AbstracSimpleChainFactory<T, D, R> {
    protected List<AbstracSimpleChainModel<T, D, R>> chainModelList;

    public AbstracSimpleChainModel<T, D, R> getChain(){
        setChainModelList();
        if(chainModelList.isEmpty() || chainModelList.size() == 0){
            return null;
        }
        for(int i=0;i+1<chainModelList.size();i++){
            chainModelList.get(i).setNextModel(chainModelList.get(i+1));
        }
        return chainModelList.get(0);
    }

    public abstract void setChainModelList();
}
