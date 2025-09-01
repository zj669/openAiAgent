package com.zj.domain.agent.adpater.repository;

import com.zj.domain.agent.model.vo.AiAgentClientFlowConfigVO;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory.DynamicContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface IAgentRepository {
    void queryApiByClientIdS(List<String> commandIdList, DynamicContext context);

    void queryModelByClientIdS(List<String> commandIdList, DynamicContext context);

    void queryMcpByClientIdS(List<String> commandIdList, DynamicContext context);

    void queryAdvisorByClientIdS(List<String> commandIdList, DynamicContext context);

    void queryPromptByClientIdS(List<String> commandIdList, DynamicContext context);

    void queryAiClientVOByClientIds(List<String> commandIdList, DynamicContext context);


    Map<String, AiAgentClientFlowConfigVO> queryAiAgentClientFlowConfig(String aiAgentId);

    List<String> queryClientIdsByAgentId(String aiAgentId);
}
