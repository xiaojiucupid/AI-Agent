package cn.bugstack.ai.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.po.AiRagOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 知识库配置数据访问接口
 */
@Mapper
public interface IAiRagOrderDao {

    /**
     * 查询所有知识库配置
     * @return 知识库配置列表
     */
    List<AiRagOrder> queryAllRagOrder();

    /**
     * 根据ID查询知识库配置
     * @param id 知识库配置ID
     * @return 知识库配置
     */
    AiRagOrder queryRagOrderById(Long id);
    
    /**
     * 根据知识库名称查询配置
     * @param ragName 知识库名称
     * @return 知识库配置
     */
    AiRagOrder queryRagOrderByName(String ragName);
    
    /**
     * 根据知识标签查询配置
     * @param knowledgeTag 知识标签
     * @return 知识库配置列表
     */
    List<AiRagOrder> queryRagOrderByTag(String knowledgeTag);
    
    /**
     * 插入知识库配置
     * @param aiRagOrder 知识库配置
     * @return 影响行数
     */
    int insert(AiRagOrder aiRagOrder);
    
    /**
     * 更新知识库配置
     * @param aiRagOrder 知识库配置
     * @return 影响行数
     */
    int update(AiRagOrder aiRagOrder);
    
    /**
     * 根据ID删除知识库配置
     * @param id 知识库配置ID
     * @return 影响行数
     */
    int deleteById(Long id);
    
    /**
     * 查询所有有效的知识库配置
     * @return 知识库配置列表
     */
    List<AiRagOrder> queryAllValidRagOrder();

    List<AiRagOrder> queryRagOrderList(AiRagOrder aiRagOrder);

}