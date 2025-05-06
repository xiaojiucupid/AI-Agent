package cn.bugstack.ai.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.po.AiClientToolMcp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MCP客户端配置数据访问接口
 */
@Mapper
public interface IAiClientToolMcpDao {

    /**
     * 查询所有MCP配置
     * @return MCP配置列表
     */
    List<AiClientToolMcp> queryAllMcpConfig();

    /**
     * 根据ID查询MCP配置
     * @param id MCP配置ID
     * @return MCP配置
     */
    AiClientToolMcp queryMcpConfigById(Long id);
    
    /**
     * 根据MCP名称查询配置
     * @param mcpName MCP名称
     * @return MCP配置
     */
    AiClientToolMcp queryMcpConfigByName(String mcpName);
    
    /**
     * 插入MCP配置
     * @param aiClientToolMcp MCP配置
     * @return 影响行数
     */
    int insert(AiClientToolMcp aiClientToolMcp);
    
    /**
     * 更新MCP配置
     * @param aiClientToolMcp MCP配置
     * @return 影响行数
     */
    int update(AiClientToolMcp aiClientToolMcp);
    
    /**
     * 根据ID删除MCP配置
     * @param id MCP配置ID
     * @return 影响行数
     */
    int deleteById(Long id);
    
    /**
     * 根据客户端ID列表查询MCP配置
     * @param clientIdList 客户端ID列表
     * @return MCP配置列表
     */
    List<AiClientToolMcp> queryMcpConfigByClientIds(List<Long> clientIdList);

    List<AiClientToolMcp> queryMcpList(AiClientToolMcp aiClientToolMcp);

}