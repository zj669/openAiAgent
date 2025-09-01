package com.zj.domain.agent.service.armory.model;

import com.zj.domain.agent.service.armory.factory.DefaultAgentArmoryFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgentArmoryVO {

    private DefaultAgentArmoryFactory.DynamicContext dynamicContext;

}
