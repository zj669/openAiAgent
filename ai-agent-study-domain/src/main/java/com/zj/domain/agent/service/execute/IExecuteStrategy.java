package com.zj.domain.agent.service.execute;

import com.zj.domain.agent.model.entity.ExecuteCommandEntity;
import org.apache.poi.ss.formula.functions.T;

public interface IExecuteStrategy {

    String execute(ExecuteCommandEntity executeCommandEntity);
}
