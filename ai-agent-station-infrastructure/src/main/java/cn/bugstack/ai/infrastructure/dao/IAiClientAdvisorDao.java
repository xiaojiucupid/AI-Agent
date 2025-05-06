package cn.bugstack.ai.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.po.AiClientAdvisor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 顾问配置数据访问接口
 */
@Mapper
public interface IAiClientAdvisorDao {

    /**
     * 查询所有顾问配置
     * @return 顾问配置列表
     */
    List<AiClientAdvisor> queryAllAdvisorConfig();

    /**
     * 根据ID查询顾问配置
     * @param id 顾问配置ID
     * @return 顾问配置
     */
    AiClientAdvisor queryAdvisorConfigById(Long id);
    
    /**
     * 根据顾问名称查询配置
     * @param advisorName 顾问名称
     * @return 顾问配置
     */
    AiClientAdvisor queryAdvisorConfigByName(String advisorName);
    
    /**
     * 插入顾问配置
     * @param aiClientAdvisor 顾问配置
     * @return 影响行数
     */
    int insert(AiClientAdvisor aiClientAdvisor);
    
    /**
     * 更新顾问配置
     * @param aiClientAdvisor 顾问配置
     * @return 影响行数
     */
    int update(AiClientAdvisor aiClientAdvisor);
    
    /**
     * 根据ID删除顾问配置
     * @param id 顾问配置ID
     * @return 影响行数
     */
    int deleteById(Long id);
    
    /**
     * 根据客户端ID列表查询顾问配置
     * @param clientIdList 客户端ID列表
     * @return 顾问配置列表
     */
    List<AiClientAdvisor> queryAdvisorConfigByClientIds(List<Long> clientIdList);

    List<AiClientAdvisor> queryClientAdvisorList(AiClientAdvisor aiClientAdvisor);

}