package com.zj.test;

import com.alibaba.fastjson2.JSON;
import com.zj.domain.agent.model.entity.ArmoryCommandEntity;
import com.zj.domain.agent.model.vo.AiClientApiVO;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory.DynamicContext;
import com.zj.domain.agent.service.armory.model.AgentArmoryVO;
import com.zj.domain.agent.service.execute.auto.factory.DefaultAutoAgentExecuteStrategyFactory;
import com.zj.domain.agent.service.execute.model.ModelExecuteStrategy;
import com.zj.infrastructure.dao.IAiClientApiDao;
import com.zj.infrastructure.dao.IAiClientToolMcpDao;
import com.zj.types.common.design.tree.handler.StrategyHandler;
import com.zj.types.enums.AiAgentEnumVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {
    @Resource
    private IAiClientToolMcpDao aiClientToolMcpDao;
    @Resource
    private DefaultAgentArmoryFactory defaultAgentArmoryFactory;
    @Resource
    protected ApplicationContext applicationContext;
    @Resource
    private ModelExecuteStrategy modelExecuteStrategy;
    @Resource
    private IAiClientApiDao aiClientApiDao;
    @Resource(name = "webClientBuilder1")
    private WebClient.Builder webClientBuilder;
    @Resource(name = "restClientBuilder1")
    private RestClient.Builder restClientBuilder;
    @Resource
    private DefaultAutoAgentExecuteStrategyFactory defaultAutoAgentExecuteStrategyFactory;

    @Test
    public void test() {
        StrategyHandler<ArmoryCommandEntity, DynamicContext, AgentArmoryVO> armoryCommandEntityDynamicContextStringStrategyHandler = defaultAgentArmoryFactory.strategyHandler();
        ArmoryCommandEntity armoryCommandEntity = new ArmoryCommandEntity();
        armoryCommandEntity.setCommandType(AiAgentEnumVO.AI_CLIENT.getLoadDataStrategy());
        armoryCommandEntity.setCommandIdList(Arrays.asList("3001"));
        DynamicContext dynamicContext = new DynamicContext();
        armoryCommandEntityDynamicContextStringStrategyHandler.apply(armoryCommandEntity, dynamicContext);
        System.out.println(JSON.toJSONString(dynamicContext));
        List<AiClientApiVO> value = dynamicContext.getValue(AiAgentEnumVO.AI_CLIENT_API.getDataName());
        Object bean = applicationContext.getBean(AiAgentEnumVO.AI_CLIENT_API.getBeanName(value.get(0).getApiId()));
        System.out.println(JSON.toJSONString( bean));
    }

    @Test
    public void test2() {
//        System.out.println(modelExecuteStrategy.execute(new ExecuteCommandEntity("6", "你是谁")));
    }

    @Test
    public void test4() {


    }

}
