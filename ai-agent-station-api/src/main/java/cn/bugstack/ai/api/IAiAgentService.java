package cn.bugstack.ai.api;

/**
 * AiAgent 智能体对话服务接口
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-05 10:14
 */
public interface IAiAgentService {

    String exec(Long aiAgentId, String message);

}
