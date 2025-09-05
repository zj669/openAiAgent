package com.zj.domain.agent.service.execute.auto.step;


import com.zj.domain.agent.model.entity.ExecuteCommandEntity;
import com.zj.domain.agent.model.vo.AiAgentClientFlowConfigVO;
import com.zj.domain.agent.service.execute.auto.factory.DefaultAutoAgentExecuteStrategyFactory;
import com.zj.domain.agent.service.execute.auto.factory.DefaultAutoAgentExecuteStrategyFactory.DynamicContext;
import com.zj.types.common.design.tree.handler.StrategyHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 执行根节点
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/7/27 16:33
 */
@Slf4j
@Service("executeRootNode")
public class RootNode extends AbstractExecuteSupport {

    @Resource
    private Step1AnalyzerNode step1AnalyzerNode;

    @Override
    protected String doApply(ExecuteCommandEntity requestParameter, DefaultAutoAgentExecuteStrategyFactory.DynamicContext dynamicContext) {
        log.info("=== 动态多轮执行测试开始 ====");
        log.info("用户输入: {}", requestParameter.getUserMessage());
        log.info("最大执行步数: {}", requestParameter.getMaxStep());
        log.info("会话ID: {}", requestParameter.getSessionId());

        Map<String, AiAgentClientFlowConfigVO> aiAgentClientFlowConfigVOMap = repository.queryAiAgentClientFlowConfig(requestParameter.getAiAgentId());

        // 客户端对话组
        dynamicContext.setAiAgentClientFlowConfigVOMap(aiAgentClientFlowConfigVOMap);
        // 上下文信息
        dynamicContext.setExecutionHistory(new StringBuilder());
        // 当前任务信息
        dynamicContext.setCurrentTask(requestParameter.getUserMessage());
        // 最大任务步骤
        dynamicContext.setMaxStep(requestParameter.getMaxStep());

        return route(requestParameter, dynamicContext);
    }


    @Override
    public StrategyHandler<ExecuteCommandEntity, DynamicContext, String> getStrategyHandler(ExecuteCommandEntity requestParams, DynamicContext context) {
        return step1AnalyzerNode;
    }
}
