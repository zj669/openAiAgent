package com.zj.test;

import com.alibaba.fastjson2.JSON;
import com.zj.domain.agent.model.entity.ArmoryCommandEntity;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory.DynamicContext;
import com.zj.infrastructure.dao.IAiClientToolMcpDao;
import com.zj.infrastructure.dao.po.AiClientToolMcp;
import com.zj.types.common.design.tree.handler.StrategyHandler;
import com.zj.types.enums.AiAgentEnumVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {
    @Resource
    private IAiClientToolMcpDao aiClientToolMcpDao;
    @Resource
    private DefaultAgentArmoryFactory defaultAgentArmoryFactory;

    @Test
    public void test() {
        StrategyHandler<ArmoryCommandEntity, DynamicContext, String> armoryCommandEntityDynamicContextStringStrategyHandler = defaultAgentArmoryFactory.strategyHandler();
        ArmoryCommandEntity armoryCommandEntity = new ArmoryCommandEntity();
        armoryCommandEntity.setCommandType(AiAgentEnumVO.AI_CLIENT.getLoadDataStrategy());
        armoryCommandEntity.setCommandIdList(Arrays.asList("3001"));
        DynamicContext dynamicContext = new DynamicContext();
        String apply = armoryCommandEntityDynamicContextStringStrategyHandler.apply(armoryCommandEntity, dynamicContext);
        System.out.println(JSON.toJSONString(dynamicContext));
    }

}
