package cn.bugstack.ai.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.po.AIClientModelToolConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI客户端模型工具配置Dao
 */
@Mapper
public interface IAIClientModelToolConfigDao {

    /**
     * 查询模型工具配置列表
     * @param modeId 模型ID
     * @return 模型工具配置列表
     */
    List<AIClientModelToolConfig> queryModelToolConfigList(Long modeId);

    /**
     * 查询模型工具配置
     * @param id 主键ID
     * @return 模型工具配置
     */
    AIClientModelToolConfig queryModelToolConfig(Integer id);

    /**
     * 新增模型工具配置
     * @param req 模型工具配置
     * @return 影响行数
     */
    int insert(AIClientModelToolConfig req);

    /**
     * 更新模型工具配置
     * @param req 模型工具配置
     * @return 影响行数
     */
    int update(AIClientModelToolConfig req);

    /**
     * 删除模型工具配置
     * @param id 主键ID
     * @return 影响行数
     */
    int delete(Integer id);

    /**
     * 根据模型ID列表查询模型工具配置
     * @param modelIds 模型ID列表
     * @return 模型工具配置列表
     */
    List<AIClientModelToolConfig> queryModelToolConfigByModelIds(List<Long> modelIds);

}