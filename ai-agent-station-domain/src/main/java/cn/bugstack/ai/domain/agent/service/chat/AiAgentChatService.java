package cn.bugstack.ai.domain.agent.service.chat;

import cn.bugstack.ai.domain.agent.adapter.repository.IAgentRepository;
import cn.bugstack.ai.domain.agent.service.IAiAgentChatService;
import cn.bugstack.ai.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * Ai智能体对话服务
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-05 10:18
 */
@Slf4j
@Service
public class AiAgentChatService implements IAiAgentChatService {

    @Resource
    private IAgentRepository repository;

    @Resource
    private DefaultArmoryStrategyFactory defaultArmoryStrategyFactory;

    @Override
    public String aiAgentChat(Long aiAgentId, String message) {
        log.info("智能体对话请求，参数 {} {}", aiAgentId, message);

        List<Long> aiClientIds = repository.queryAiClientIdsByAiAgentId(aiAgentId);

        String content = "";

        for (Long aiClientId : aiClientIds) {
            ChatClient chatClient = defaultArmoryStrategyFactory.chatClient(aiClientId);

            content = chatClient.prompt(message + "，" + content)
                    .system(s -> s.param("current_date", LocalDate.now().toString()))
                    .advisors(a -> a
                            .param(CHAT_MEMORY_CONVERSATION_ID_KEY, "chatId-101")
                            .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                    .call().content();

            log.info("智能体对话进行，客户端ID {}", aiClientId);
        }

        log.info("智能体对话请求，结果 {} {}", aiAgentId, content);

        return content;
    }

}
