package com.zj.domain.agent.service.execute.auto;


import com.zj.domain.agent.adpater.repository.IAgentRepository;
import com.zj.domain.agent.model.entity.ExecuteCommandEntity;
import com.zj.domain.agent.service.execute.AbstractExecuteStrategy;
import com.zj.types.common.design.tree.AbsractMultiTreadStrategyRouter;
import com.zj.types.enums.AiAgentEnumVO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/7/27 16:48
 */
public abstract class AbstractExecuteSupport extends AbsractMultiTreadStrategyRouter<ExecuteCommandEntity, AbstractExecuteStrategy.DynamicContext, String> {

    private final Logger log = LoggerFactory.getLogger(AbstractExecuteSupport.class);

    @Resource
    protected ApplicationContext applicationContext;

    @Resource
    protected IAgentRepository repository;

    public static final String CHAT_MEMORY_CONVERSATION_ID_KEY = "chat_memory_conversation_id";
    public static final String CHAT_MEMORY_RETRIEVE_SIZE_KEY = "chat_memory_response_size";

    @Override
    protected void multiThread(ExecuteCommandEntity requestParameter,  AbstractExecuteStrategy.DynamicContext dynamicContext)  {

    }

    protected ChatClient getChatClientByClientId(String clientId) {
        return getBean(AiAgentEnumVO.AI_CLIENT.getBeanName(clientId));
    }

    protected <T> T getBean(String beanName) {
        return (T) applicationContext.getBean(beanName);
    }

}
