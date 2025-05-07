package cn.bugstack.ai.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.po.AiClientModelConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI客户端模型配置数据访问接口
 */
@Mapper
public interface IAiClientModelConfigDao {

    /**
     * 查询所有客户端模型配置
     * @return 客户端模型配置列表
     */
    List<AiClientModelConfig> queryAllModelConfig();

    /**
     * 根据ID查询客户端模型配置
     * @param id 客户端模型配置ID
     * @return 客户端模型配置
     */
    AiClientModelConfig queryModelConfigById(Long id);
    
    /**
     * 根据客户端ID查询模型配置
     * @param clientId 客户端ID
     * @return 客户端模型配置
     */
    AiClientModelConfig queryModelConfigByClientId(Long clientId);
    
    /**
     * 插入客户端模型配置
     * @param aiClientModelConfig 客户端模型配置
     * @return 影响行数
     */
    int insert(AiClientModelConfig aiClientModelConfig);
    
    /**
     * 更新客户端模型配置
     * @param aiClientModelConfig 客户端模型配置
     * @return 影响行数
     */
    int update(AiClientModelConfig aiClientModelConfig);
    
    /**
     * 根据ID删除客户端模型配置
     * @param id 客户端模型配置ID
     * @return 影响行数
     */
    int deleteById(Long id);
    
    /**
     * 根据客户端ID列表查询模型配置
     * @param clientIdList 客户端ID列表
     * @return 客户端模型配置列表
     */
    List<AiClientModelConfig> queryModelConfigByClientIds(List<Long> clientIdList);
    
    /**
     * 根据模型ID查询客户端模型配置列表
     * @param modelId 模型ID
     * @return 客户端模型配置列表
     */
    List<AiClientModelConfig> queryModelConfigByModelId(Long modelId);

    Long queryAiClientModelIdByAgentId(Long aiAgentId);

}