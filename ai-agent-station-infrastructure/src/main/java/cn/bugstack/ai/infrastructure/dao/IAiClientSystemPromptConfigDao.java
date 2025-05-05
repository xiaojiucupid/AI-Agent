package cn.bugstack.ai.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.po.AiClientSystemPromptConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 系统提示词配置关联数据访问接口
 */
@Mapper
public interface IAiClientSystemPromptConfigDao {

    /**
     * 查询所有系统提示词配置关联
     * @return 系统提示词配置关联列表
     */
    List<AiClientSystemPromptConfig> queryAllSystemPromptConfig();

    /**
     * 根据ID查询系统提示词配置关联
     * @param id 系统提示词配置关联ID
     * @return 系统提示词配置关联
     */
    AiClientSystemPromptConfig querySystemPromptConfigById(Long id);
    
    /**
     * 根据客户端ID查询系统提示词配置关联
     * @param clientId 客户端ID
     * @return 系统提示词配置关联
     */
    AiClientSystemPromptConfig querySystemPromptConfigByClientId(Long clientId);
    
    /**
     * 插入系统提示词配置关联
     * @param aiClientSystemPromptConfig 系统提示词配置关联
     * @return 影响行数
     */
    int insert(AiClientSystemPromptConfig aiClientSystemPromptConfig);
    
    /**
     * 更新系统提示词配置关联
     * @param aiClientSystemPromptConfig 系统提示词配置关联
     * @return 影响行数
     */
    int update(AiClientSystemPromptConfig aiClientSystemPromptConfig);
    
    /**
     * 根据ID删除系统提示词配置关联
     * @param id 系统提示词配置关联ID
     * @return 影响行数
     */
    int deleteById(Long id);
    
    /**
     * 根据客户端ID列表查询系统提示词配置关联
     * @param clientIdList 客户端ID列表
     * @return 系统提示词配置关联列表
     */
    List<AiClientSystemPromptConfig> querySystemPromptConfigByClientIds(List<Long> clientIdList);
}