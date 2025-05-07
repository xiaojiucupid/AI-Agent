package cn.bugstack.ai.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.po.AiClientModel;
import cn.bugstack.ai.infrastructure.dao.po.AiClientToolMcp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI模型配置数据访问接口
 */
@Mapper
public interface IAiClientModelDao {

    /**
     * 查询所有模型配置
     * @return 模型配置列表
     */
    List<AiClientModel> queryAllModelConfig();

    /**
     * 根据ID查询模型配置
     * @param id 模型配置ID
     * @return 模型配置
     */
    AiClientModel queryModelConfigById(Long id);
    
    /**
     * 根据模型名称查询模型配置
     * @param modelName 模型名称
     * @return 模型配置
     */
    AiClientModel queryModelConfigByName(String modelName);
    
    /**
     * 插入模型配置
     * @param aiClientModel 模型配置
     * @return 影响行数
     */
    int insert(AiClientModel aiClientModel);
    
    /**
     * 更新模型配置
     * @param aiClientModel 模型配置
     * @return 影响行数
     */
    int update(AiClientModel aiClientModel);
    
    /**
     * 根据ID删除模型配置
     * @param id 模型配置ID
     * @return 影响行数
     */
    int deleteById(Long id);
    
    /**
     * 根据客户端ID列表查询模型配置
     * @param clientIdList 客户端ID列表
     * @return 模型配置列表
     */
    List<AiClientModel> queryModelConfigByClientIds(List<Long> clientIdList);

    List<AiClientToolMcp> queryToolMcpConfigByClientIds(List<Long> clientIdList);
    
    /**
     * 根据条件查询客户端模型列表
     * @param aiClientModel 查询条件
     * @return 客户端模型列表
     */
    List<AiClientModel> queryClientModelList(AiClientModel aiClientModel);

}
