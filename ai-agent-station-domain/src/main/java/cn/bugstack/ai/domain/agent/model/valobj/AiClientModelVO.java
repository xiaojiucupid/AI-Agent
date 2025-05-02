package cn.bugstack.ai.domain.agent.model.valobj;

import lombok.*;

/**
 * Ai 客户端模型值对象
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-02 14:19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiClientModelVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 基础URL
     */
    private String baseUrl;

    /**
     * API密钥
     */
    private String apiKey;

    /**
     * 完成路径
     */
    private String completionsPath;

    /**
     * 嵌入路径
     */
    private String embeddingsPath;

    /**
     * 模型类型(openai/azure等)
     */
    private String modelType;

    /**
     * 模型版本
     */
    private String modelVersion;

    /**
     * 超时时间(秒)
     */
    private Integer timeout;

}
