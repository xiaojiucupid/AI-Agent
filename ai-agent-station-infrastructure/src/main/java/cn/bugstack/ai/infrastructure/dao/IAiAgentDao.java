package cn.bugstack.ai.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.po.AiAgent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * AI智能体配置数据访问接口
 */
@Mapper
public interface IAiAgentDao {

    /**
     * 查询所有智能体配置
     * @return 智能体配置列表
     */
    List<AiAgent> queryAllAgentConfig();

    /**
     * 根据ID查询智能体配置
     * @param id 智能体配置ID
     * @return 智能体配置
     */
    AiAgent queryAgentConfigById(Long id);
    
    /**
     * 根据智能体名称查询配置
     * @param agentName 智能体名称
     * @return 智能体配置
     */
    AiAgent queryAgentConfigByName(String agentName);
    
    /**
     * 插入智能体配置
     * @param aiAgent 智能体配置
     * @return 影响行数
     */
    int insert(AiAgent aiAgent);
    
    /**
     * 更新智能体配置
     * @param aiAgent 智能体配置
     * @return 影响行数
     */
    int update(AiAgent aiAgent);
    
    /**
     * 根据ID删除智能体配置
     * @param id 智能体配置ID
     * @return 影响行数
     */
    int deleteById(Long id);
    
    /**
     * 查询有效的智能体ID及其关联的客户端ID集合
     * @return 智能体ID与客户端ID集合的映射
     */
    List<Map<String, Object>> queryValidAgentWithClientIds();
    
    /**
     * 查询所有有效的智能体关联的客户端ID
     * @return 客户端ID列表
     */
    List<Long> queryValidClientIds();
    
    /**
     * 根据智能体ID查询关联的客户端ID列表
     * @param agentId 智能体ID
     * @return 客户端ID列表
     */
    List<Long> queryClientIdsByAgentId(@Param("agentId") Long agentId);
    
    /**
     * 根据条件查询智能体列表
     * @param aiAgent 查询条件
     * @return 智能体列表
     */
    List<AiAgent> queryAiAgentList(AiAgent aiAgent);

    List<AiAgent> queryAllAgentConfigByChannel(String chatStream);

}