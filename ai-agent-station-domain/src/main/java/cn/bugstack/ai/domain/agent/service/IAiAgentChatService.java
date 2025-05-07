package cn.bugstack.ai.domain.agent.service;

import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

/**
 * Ai智能体对话服务接口
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-05 10:17
 */
public interface IAiAgentChatService {

    /**
     * 智能体对话
     */
    String aiAgentChat(Long aiAgentId, String message);

    Flux<ChatResponse> aiAgentChatStream(Long aiAgentId, Long ragId, String message);

}
