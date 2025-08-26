package com.zj.domain.agent.service.armory.strategy;

import com.zj.domain.agent.adpater.repository.IAgentRepository;
import com.zj.domain.agent.model.entity.ArmoryCommandEntity;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory.DynamicContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service("aiClientLoadDataStrategy")
@Slf4j
public class ClientLoadDataStrategy implements ILoadDataStrategy{
    @Resource
    private IAgentRepository agentRepository;
    @Resource
    protected ThreadPoolExecutor threadPoolExecutor;
    @Override
    public void loadData(ArmoryCommandEntity requestParams, DynamicContext context) {
        log.info("开始异步加载数据");
        // api
        CompletableFuture<Void> futureApi = CompletableFuture.runAsync(() -> {
            agentRepository.queryApiByClientIdS(requestParams.getCommandIdList(), context);
        }, threadPoolExecutor);
        // model
        CompletableFuture<Void> futureModel = CompletableFuture.runAsync(() -> {
            agentRepository.queryModelByClientIdS(requestParams.getCommandIdList(), context);
        }, threadPoolExecutor);
        // mcp
        CompletableFuture<Void> futureMcp = CompletableFuture.runAsync(() -> {
            agentRepository.queryMcpByClientIdS(requestParams.getCommandIdList(), context);
        }, threadPoolExecutor);
        // advisor
        CompletableFuture<Void> futureAdvisor = CompletableFuture.runAsync(() -> {
            agentRepository.queryAdvisorByClientIdS(requestParams.getCommandIdList(), context);
        }, threadPoolExecutor);
        // prompt
        CompletableFuture<Void> futurePrompt = CompletableFuture.runAsync(() -> {
            agentRepository.queryPromptByClientIdS(requestParams.getCommandIdList(), context);
        }, threadPoolExecutor);

        CompletableFuture<Void> future = CompletableFuture.allOf(futureApi, futureModel, futureMcp, futureAdvisor, futurePrompt);
        future.join();
        log.info("结束异步加载数据");
    }
}
