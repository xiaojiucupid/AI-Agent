package cn.bugstack.ai.domain.agent.service.chat;

import cn.bugstack.ai.domain.agent.adapter.repository.IAgentRepository;
import cn.bugstack.ai.domain.agent.service.IAiAgentChatService;
import cn.bugstack.ai.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private PgVectorStore vectorStore;

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

    @Override
    public Flux<ChatResponse> aiAgentChatStream(Long aiAgentId, Long ragId, String message) {
        log.info("智能体对话请求，参数 aiAgentId {} message {}", aiAgentId, message);

        // 查询模型ID
        Long modelId = repository.queryAiClientModelIdByAgentId(aiAgentId);

        // 获取对话模型
        ChatModel chatModel = defaultArmoryStrategyFactory.chatModel(modelId);

        // 封装请求参数
        List<Message> messages = new ArrayList<>();

        if (null != ragId && 0 != ragId){
            // 查询RAG标签
            String tag = repository.queryRagKnowledgeTag(ragId);

            SearchRequest searchRequest = SearchRequest.builder()
                    .query(message)
                    .topK(5)
                    .filterExpression("knowledge == '" + tag + "'")
                    .build();

            List<Document> documents = vectorStore.similaritySearch(searchRequest);
            String documentCollectors = documents.stream().map(Document::getFormattedContent).collect(Collectors.joining());
            Message ragMessage = new SystemPromptTemplate("""
                Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
                If unsure, simply state that you don't know.
                Another thing you need to note is that your reply must be in Chinese!
                DOCUMENTS:
                    {documents}
                """).createMessage(Map.of("documents", documentCollectors));

            messages.add(new UserMessage(message));
            messages.add(ragMessage);
        } else {
            messages.add(new UserMessage(message));
        }

        return chatModel.stream(Prompt.builder()
                .messages(messages)
                .build());
    }

}
