package cn.bugstack.ai.infrastructure.dao.po;

import lombok.Data;

import java.util.Date;

/**
 * 系统提示词配置表映射
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-05 10:50
 */
@Data
public class AiClientSystemPromptConfig {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 客户端ID
     */
    private Long clientId;

    /**
     * 系统提示词ID
     */
    private Long systemPromptId;

    /**
     * 创建时间
     */
    private Date createTime;

}
