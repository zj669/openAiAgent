package com.zj.domain.agent.service.armory.node;

import com.zj.domain.agent.model.entity.ArmoryCommandEntity;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory.DynamicContext;
import com.zj.domain.agent.service.armory.model.AgentArmoryVO;
import com.zj.types.common.design.tree.AbsractMultiTreadStrategyRouter;
import com.zj.types.utills.SpringContextUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public abstract class AgentAromorSupport extends AbsractMultiTreadStrategyRouter<ArmoryCommandEntity, DefaultAgentArmoryFactory.DynamicContext, AgentArmoryVO> {
    private final Logger log = LoggerFactory.getLogger(AgentAromorSupport.class);
    @Resource
    protected SpringContextUtil springContextUtil ;

    @Override
    protected void multiThread(ArmoryCommandEntity requestParams, DynamicContext context) {

    }

    /**
     * 通用的Bean注册方法
     *
     * @param beanName  Bean名称
     * @param beanClass Bean类型
     * @param <T>       Bean类型
     */
    protected synchronized <T> void registerBean(String beanName, Class<T> beanClass, T beanInstance) {
       springContextUtil.registerBean(beanName, beanClass, beanInstance);
    }

    protected <T> T getBean(String beanName) {
        return springContextUtil.getBean(beanName);
    }

    protected abstract String beanName(String beanId);


    protected abstract String dataName();
}
