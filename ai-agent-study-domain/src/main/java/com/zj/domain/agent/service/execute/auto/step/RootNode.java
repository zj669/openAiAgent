package com.zj.domain.agent.service.execute.auto.step;


import com.zj.domain.agent.adpater.repository.IAgentRepository;
import com.zj.domain.agent.model.entity.ArmoryCommandEntity;
import com.zj.domain.agent.model.entity.ExecuteCommandEntity;
import com.zj.domain.agent.model.vo.AiAgentClientFlowConfigVO;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory;
import com.zj.domain.agent.service.armory.model.AgentArmoryVO;
import com.zj.domain.agent.service.execute.AbstractExecuteStrategy;
import com.zj.domain.agent.service.execute.auto.AbstractExecuteSupport;
import com.zj.types.common.design.tree.handler.StrategyHandler;
import com.zj.types.enums.AiAgentEnumVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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
    @Resource
    private DefaultAgentArmoryFactory agentArmoryFactory;
    @Resource
    private IAgentRepository agentRepository;

    @Override
    protected String doApply(ExecuteCommandEntity requestParameter, AbstractExecuteStrategy.DynamicContext dynamicContext){
        log.info("=== 动态多轮执行测试开始 ====");
        log.info("用户输入: {}", requestParameter.getUserMessage());
        log.info("最大执行步数: {}", requestParameter.getMaxStep());
        log.info("会话ID: {}", requestParameter.getSessionId());



        Map<String, AiAgentClientFlowConfigVO> aiAgentClientFlowConfigVOMap = repository.queryAiAgentClientFlowConfig(requestParameter.getAiAgentId());
        log.info("AI代理ID: {}", requestParameter.getAiAgentId());
        StrategyHandler<ArmoryCommandEntity, DefaultAgentArmoryFactory.DynamicContext, AgentArmoryVO> armoryCommandEntityDynamicContextAgentArmoryVOStrategyHandler = agentArmoryFactory.strategyHandler();
        List<String> commandIdList  = agentRepository.queryClientIdsByAgentId(requestParameter.getAiAgentId());
        ArmoryCommandEntity armoryCommandEntity = ArmoryCommandEntity.builder()
                .commandType(AiAgentEnumVO.AI_CLIENT.getLoadDataStrategy())
                .commandIdList(commandIdList)
                .build();
        armoryCommandEntityDynamicContextAgentArmoryVOStrategyHandler.apply(armoryCommandEntity, new DefaultAgentArmoryFactory.DynamicContext());

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
    public StrategyHandler<ExecuteCommandEntity, AbstractExecuteStrategy.DynamicContext, String> getStrategyHandler(ExecuteCommandEntity requestParameter, AbstractExecuteStrategy.DynamicContext dynamicContext) {
        return step1AnalyzerNode;
    }

}
