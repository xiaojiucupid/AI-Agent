package cn.bugstack.ai.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.po.AiAgentTaskSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 智能体任务调度配置数据访问接口
 */
@Mapper
public interface IAiAgentTaskScheduleDao {

    /**
     * 查询所有任务调度配置
     * @return 任务调度配置列表
     */
    List<AiAgentTaskSchedule> queryAllTaskSchedule();

    /**
     * 根据ID查询任务调度配置
     * @param id 任务调度配置ID
     * @return 任务调度配置
     */
    AiAgentTaskSchedule queryTaskScheduleById(Long id);
    
    /**
     * 根据智能体ID查询任务调度配置
     * @param agentId 智能体ID
     * @return 任务调度配置列表
     */
    List<AiAgentTaskSchedule> queryTaskScheduleByAgentId(Long agentId);
    
    /**
     * 插入任务调度配置
     * @param aiAgentTaskSchedule 任务调度配置
     * @return 影响行数
     */
    int insert(AiAgentTaskSchedule aiAgentTaskSchedule);
    
    /**
     * 更新任务调度配置
     * @param aiAgentTaskSchedule 任务调度配置
     * @return 影响行数
     */
    int update(AiAgentTaskSchedule aiAgentTaskSchedule);
    
    /**
     * 根据ID删除任务调度配置
     * @param id 任务调度配置ID
     * @return 影响行数
     */
    int deleteById(Long id);
    
    /**
     * 根据智能体ID删除任务调度配置
     * @param agentId 智能体ID
     * @return 影响行数
     */
    int deleteByAgentId(Long agentId);
    
    /**
     * 查询所有有效的任务调度配置
     * @return 有效的任务调度配置列表
     */
    List<AiAgentTaskSchedule> queryAllValidTaskSchedule();

    List<Long> queryAllInvalidTaskScheduleIds();

    List<AiAgentTaskSchedule> queryTaskScheduleList(AiAgentTaskSchedule aiAgentTaskSchedule);

}