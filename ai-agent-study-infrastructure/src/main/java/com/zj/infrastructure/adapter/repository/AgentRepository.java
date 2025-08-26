package com.zj.infrastructure.adapter.repository;

import com.zj.domain.agent.adpater.repository.IAgentRepository;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory.DynamicContext;
import com.zj.infrastructure.dao.IAiAgentDao;
import com.zj.infrastructure.dao.IAiAgentFlowConfigDao;
import com.zj.infrastructure.dao.IAiAgentTaskScheduleDao;
import com.zj.infrastructure.dao.IAiClientAdvisorDao;
import com.zj.infrastructure.dao.IAiClientApiDao;
import com.zj.infrastructure.dao.IAiClientConfigDao;
import com.zj.infrastructure.dao.IAiClientDao;
import com.zj.infrastructure.dao.IAiClientModelDao;
import com.zj.infrastructure.dao.IAiClientRagOrderDao;
import com.zj.infrastructure.dao.IAiClientSystemPromptDao;
import com.zj.infrastructure.dao.IAiClientToolMcpDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class AgentRepository implements IAgentRepository {
    @Resource
    private IAiAgentDao aiAgentDao;

    @Resource
    private IAiAgentFlowConfigDao aiAgentFlowConfigDao;

    @Resource
    private IAiAgentTaskScheduleDao aiAgentTaskScheduleDao;

    @Resource
    private IAiClientAdvisorDao aiClientAdvisorDao;

    @Resource
    private IAiClientApiDao aiClientApiDao;

    @Resource
    private IAiClientConfigDao aiClientConfigDao;

    @Resource
    private IAiClientDao aiClientDao;

    @Resource
    private IAiClientModelDao aiClientModelDao;

    @Resource
    private IAiClientRagOrderDao aiClientRagOrderDao;

    @Resource
    private IAiClientSystemPromptDao aiClientSystemPromptDao;

    @Resource
    private IAiClientToolMcpDao aiClientToolMcpDao;

    @Override
    public void queryApiByClientIdS(List<String> commandIdList, DynamicContext context) {
        // 先查client对应的 api
        // 获取api

    }

    @Override
    public void queryModelByClientIdS(List<String> commandIdList, DynamicContext context) {

    }

    @Override
    public void queryMcpByClientIdS(List<String> commandIdList, DynamicContext context) {

    }

    @Override
    public void queryAdvisorByClientIdS(List<String> commandIdList, DynamicContext context) {

    }

    @Override
    public void queryPromptByClientIdS(List<String> commandIdList, DynamicContext context) {

    }
}
