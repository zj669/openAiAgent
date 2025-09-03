package com.zj.domain.agent.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteCommandEntity {
    private String aiAgentId;
    private String userMessage;
    private Integer maxStep;
    private String sessionId;
}
