package cn.bugstack.ai.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.po.AiClientSystemPrompt;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 系统提示词配置数据访问接口
 */
@Mapper
public interface IAiClientSystemPromptDao {

    /**
     * 查询所有系统提示词配置
     * @return 系统提示词配置列表
     */
    List<AiClientSystemPrompt> queryAllSystemPromptConfig();

    /**
     * 根据ID查询系统提示词配置
     * @param id 系统提示词配置ID
     * @return 系统提示词配置
     */
    AiClientSystemPrompt querySystemPromptConfigById(Long id);
    
    /**
     * 根据提示词名称查询配置
     * @param promptName 提示词名称
     * @return 系统提示词配置
     */
    AiClientSystemPrompt querySystemPromptConfigByName(String promptName);
    
    /**
     * 插入系统提示词配置
     * @param aiClientSystemPrompt 系统提示词配置
     * @return 影响行数
     */
    int insert(AiClientSystemPrompt aiClientSystemPrompt);
    
    /**
     * 更新系统提示词配置
     * @param aiClientSystemPrompt 系统提示词配置
     * @return 影响行数
     */
    int update(AiClientSystemPrompt aiClientSystemPrompt);
    
    /**
     * 根据ID删除系统提示词配置
     * @param id 系统提示词配置ID
     * @return 影响行数
     */
    int deleteById(Long id);
    
    /**
     * 根据条件查询系统提示词列表
     * @param aiClientSystemPrompt 查询条件
     * @return 系统提示词列表
     */
    List<AiClientSystemPrompt> querySystemPromptList(AiClientSystemPrompt aiClientSystemPrompt);

    List<AiClientSystemPrompt> querySystemPromptConfigByClientIds(List<Long> clientIdList);

}