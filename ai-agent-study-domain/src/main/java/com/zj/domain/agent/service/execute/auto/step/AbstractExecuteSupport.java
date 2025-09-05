package com.zj.domain.agent.service.execute.auto.step;


import com.alibaba.fastjson.JSON;
import com.zj.domain.agent.adpater.repository.IAgentRepository;
import com.zj.domain.agent.model.entity.AutoAgentExecuteResultEntity;
import com.zj.domain.agent.model.entity.ExecuteCommandEntity;
import com.zj.domain.agent.service.execute.auto.factory.DefaultAutoAgentExecuteStrategyFactory;
import com.zj.domain.agent.service.execute.auto.factory.DefaultAutoAgentExecuteStrategyFactory.DynamicContext;
import com.zj.types.common.design.tree.AbsractMultiTreadStrategyRouter;
import com.zj.types.enums.AiAgentEnumVO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;

/**
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/7/27 16:48
 */
public abstract class AbstractExecuteSupport extends AbsractMultiTreadStrategyRouter<ExecuteCommandEntity, DynamicContext, String> {

    private final Logger log = LoggerFactory.getLogger(AbstractExecuteSupport.class);

    @Resource
    protected ApplicationContext applicationContext;

    @Resource
    protected IAgentRepository repository;

    public static final String CHAT_MEMORY_CONVERSATION_ID_KEY = "chat_memory_conversation_id";
    public static final String CHAT_MEMORY_RETRIEVE_SIZE_KEY = "chat_memory_response_size";

    @Override
    protected void multiThread(ExecuteCommandEntity requestParameter, DefaultAutoAgentExecuteStrategyFactory.DynamicContext dynamicContext) {

    }

    protected ChatClient getChatClientByClientId(String clientId) {
        return getBean(AiAgentEnumVO.AI_CLIENT.getBeanName(clientId));
    }

    protected <T> T getBean(String beanName) {
        return (T) applicationContext.getBean(beanName);
    }

    /**
     * 通用的SSE结果发送方法
     * @param dynamicContext 动态上下文
     * @param result 要发送的结果实体
     */
    protected void sendSseResult(DefaultAutoAgentExecuteStrategyFactory.DynamicContext dynamicContext,
                                 AutoAgentExecuteResultEntity result) {
        try {
            ResponseBodyEmitter emitter = dynamicContext.getValue("emitter");
            if (emitter != null) {
                // 发送SSE格式的数据
                String sseData = "data: " + JSON.toJSONString(result) + "\n\n";
                emitter.send(sseData);
            }
        } catch (IOException e) {
            log.error("发送SSE结果失败：{}", e.getMessage(), e);
        }
    }

}
