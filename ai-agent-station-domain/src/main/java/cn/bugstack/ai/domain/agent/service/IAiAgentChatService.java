package cn.bugstack.ai.domain.agent.service;

/**
 * Ai智能体对话服务接口
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-05 10:17
 */
public interface IAiAgentChatService {

    String aiAgentChat(Long aiAgentId, String message);

}
