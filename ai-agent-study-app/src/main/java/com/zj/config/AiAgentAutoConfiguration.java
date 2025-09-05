package com.zj.config;

import com.zj.domain.agent.model.entity.ArmoryCommandEntity;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory;
import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory.DynamicContext;
import com.zj.domain.agent.service.armory.model.AgentArmoryVO;
import com.zj.types.common.Constants;
import com.zj.types.common.design.tree.handler.StrategyHandler;
import com.zj.types.enums.AiAgentEnumVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableConfigurationProperties(AiAgentAutoConfigProperties.class)
@ConditionalOnProperty(prefix = "spring.ai.agent.auto-config", name = "enabled", havingValue = "true")
public class AiAgentAutoConfiguration implements ApplicationListener<ApplicationReadyEvent> {

    @Resource
    private AiAgentAutoConfigProperties aiAgentAutoConfigProperties;

    @Resource
    private DefaultAgentArmoryFactory defaultArmoryStrategyFactory;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            log.info("AI Agent 自动装配开始，配置: {}", aiAgentAutoConfigProperties);
            
            // 检查配置是否有效
            if (!aiAgentAutoConfigProperties.isEnabled()) {
                log.info("AI Agent 自动装配未启用");
                return;
            }

            List<String> clientIds = aiAgentAutoConfigProperties.getClientIds();
            if (CollectionUtils.isEmpty(clientIds)) {
                log.warn("AI Agent 自动装配配置的客户端ID列表为空");
                return;
            }

            // 解析客户端ID列表（支持逗号分隔的字符串）
            List<String> commandIdList;
            if (clientIds.size() == 1 && clientIds.get(0).contains(Constants.SPLIT)) {
                // 处理逗号分隔的字符串
                commandIdList = Arrays.stream(clientIds.get(0).split(Constants.SPLIT))
                        .map(String::trim)
                        .filter(id -> !id.isEmpty())
                        .collect(Collectors.toList());
            } else {
                commandIdList = clientIds;
            }

            log.info("开始自动装配AI客户端，客户端ID列表: {}", commandIdList);

            // 执行自动装配
            StrategyHandler<ArmoryCommandEntity, DynamicContext, AgentArmoryVO> armoryStrategyHandler =
                    defaultArmoryStrategyFactory.strategyHandler();

            AgentArmoryVO result = armoryStrategyHandler.apply(
                    ArmoryCommandEntity.builder()
                            .commandType(AiAgentEnumVO.AI_CLIENT.getLoadDataStrategy())
                            .commandIdList(commandIdList)
                            .build(),
                    new DefaultAgentArmoryFactory.DynamicContext());

            log.info("AI Agent 自动装配完成，结果: {}", result);
            
        } catch (Exception e) {
            log.error("AI Agent 自动装配失败", e);
        }
    }

}