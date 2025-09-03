package com.zj.domain.agent.service.execute;

import com.zj.domain.agent.model.entity.ExecuteCommandEntity;

public interface IExecuteStrategy {

    String execute(ExecuteCommandEntity executeCommandEntity);
}
