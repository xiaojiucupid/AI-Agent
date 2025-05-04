package cn.bugstack.ai.test.domain;

import cn.bugstack.ai.domain.agent.model.entity.AiAgentEngineStarterEntity;
import cn.bugstack.ai.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import cn.bugstack.ai.domain.agent.service.armory.node.RootNode;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * 功能测试
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-04 07:29
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AgentTest {

    @Resource
    private DefaultArmoryStrategyFactory defaultArmoryStrategyFactory;

    @Test
    public void test() throws Exception {
        StrategyHandler<AiAgentEngineStarterEntity, DefaultArmoryStrategyFactory.DynamicContext, String> handler = defaultArmoryStrategyFactory.strategyHandler();

        AiAgentEngineStarterEntity aiAgentEngineStarterEntity = new AiAgentEngineStarterEntity();
        aiAgentEngineStarterEntity.setClientIdList(new ArrayList<>() {{
            add(1L);
        }});

        String apply = handler.apply(aiAgentEngineStarterEntity, new DefaultArmoryStrategyFactory.DynamicContext());

        log.info("测试结果：{}", apply);

        ChatClient chatClient = defaultArmoryStrategyFactory.chatClient(1L);

        String userInput = "有哪些工具可以使用";
        System.out.println("\n>>> QUESTION: " + userInput);
        System.out.println("\n>>> ASSISTANT: " + chatClient
                .prompt(userInput)
                .system(s -> s.param("current_date", LocalDate.now().toString()))
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, "chatId-101")
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call().content());
    }

}
