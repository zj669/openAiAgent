package com.zj.domain.agent.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 聊天模型配置，值对象
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/6/27 17:43
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiClientModelVO {

    /**
     * 全局唯一模型ID
     */
    private String modelId;

    /**
     * 关联的API配置ID
     */
    private String apiId;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 模型类型：openai、deepseek、claude
     */
    private String modelType;

    /**
     * 工具 mcp ids
     */
    private List<String> toolMcpIds;

}
