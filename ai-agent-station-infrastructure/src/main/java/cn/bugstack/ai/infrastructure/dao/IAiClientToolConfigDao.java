package cn.bugstack.ai.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.po.AiClientToolConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户端工具配置DAO接口
 */
@Mapper
public interface IAiClientToolConfigDao {

    /**
     * 查询所有客户端工具配置
     * @return 客户端工具配置列表
     */
    List<AiClientToolConfig> queryAllToolConfig();

    /**
     * 根据ID查询客户端工具配置
     * @param id 客户端工具配置ID
     * @return 客户端工具配置
     */
    AiClientToolConfig queryToolConfigById(Long id);
    
    /**
     * 根据客户端ID查询工具配置列表
     * @param clientId 客户端ID
     * @return 客户端工具配置列表
     */
    List<AiClientToolConfig> queryToolConfigByClientId(Long clientId);
    
    /**
     * 插入客户端工具配置
     * @param aiClientToolConfig 客户端工具配置
     * @return 影响行数
     */
    int insert(AiClientToolConfig aiClientToolConfig);
    
    /**
     * 更新客户端工具配置
     * @param aiClientToolConfig 客户端工具配置
     * @return 影响行数
     */
    int update(AiClientToolConfig aiClientToolConfig);
    
    /**
     * 根据ID删除客户端工具配置
     * @param id 客户端工具配置ID
     * @return 影响行数
     */
    int deleteById(Long id);
    
    /**
     * 根据客户端ID和工具ID删除客户端工具配置
     * @param clientId 客户端ID
     * @param toolId 工具ID
     * @return 影响行数
     */
    int deleteByClientIdAndToolId(@Param("clientId") Long clientId, @Param("toolId") Long toolId);
    
    /**
     * 根据客户端ID列表查询工具配置
     * @param clientIdList 客户端ID列表
     * @return 客户端工具配置列表
     */
    List<AiClientToolConfig> queryToolConfigByClientIds(@Param("clientIdList") List<Long> clientIdList);
    
    /**
     * 根据工具ID查询客户端工具配置列表
     * @param toolId 工具ID
     * @return 客户端工具配置列表
     */
    List<AiClientToolConfig> queryToolConfigByToolId(Long toolId);
    
    /**
     * 根据工具类型查询客户端工具配置列表
     * @param toolType 工具类型
     * @return 客户端工具配置列表
     */
    List<AiClientToolConfig> queryToolConfigByToolType(String toolType);

    List<AiClientToolConfig> queryToolConfigList(AiClientToolConfig aiClientToolConfig);

}