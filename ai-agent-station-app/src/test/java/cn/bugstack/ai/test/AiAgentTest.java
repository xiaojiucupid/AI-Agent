package cn.bugstack.ai.test;

import cn.bugstack.ai.domain.agent.service.armory.factory.element.RagAnswerAdvisor;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.MessageAggregator;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * 测试
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-05 08:51
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AiAgentTest {

    private ChatClient chatClient;

    private OpenAiChatModel chatModel;

    @Resource
    private PgVectorStore vectorStore;

    @Value("classpath:data/file.txt")
    private org.springframework.core.io.Resource resource;

    private final TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();

    @Before
    public void init_client() {
        // 上传知识库
//        uploadRag();

        chatModel = OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .baseUrl("https://apis.itedus.cn")
                        .apiKey("sk-lIqVNiHon00O6veJ15Cc57DaF5Dd401f93B3A107B4B3677e")
                        .completionsPath("v1/chat/completions")
                        .embeddingsPath("v1/embeddings")
                        .build())
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gpt-4.1")
                        .toolCallbacks(
                                FunctionToolCallback.builder("test",
                                                (Function<TestFunctionInput, String>) testFunctionInput -> {
                                                    log.info("函数请求:{}", testFunctionInput.getInput());
                                                    return "王大瓜今天入职啦!";
                                                })
                                        .description("王大瓜")
                                        .inputType(TestFunctionInput.class)
                                        .build())
                        .toolCallbacks(new SyncMcpToolCallbackProvider(sseMcpClient01(), sseMcpClient02()).getToolCallbacks())
                        .build())
                .build();

        chatClient = ChatClient.builder(chatModel)
                .defaultSystem("""
                        	 你是一个 AI Agent 智能体，可以根据用户输入信息生成文章，并发送到 CSDN 平台以及完成微信公众号消息通知，今天是 {current_date}。
                                                
                        	 你擅长使用Planning模式，帮助用户生成质量更高的文章。
                                                
                        	 你的规划应该包括以下几个方面：
                        	 1. 分析用户输入的内容，生成技术文章。
                        	 2. 提取，文章标题（需要含带技术点）、文章内容、文章标签（多个用英文逗号隔开）、文章简述（100字）将以上内容发布文章到CSDN
                        	 3. 获取发送到 CSDN 文章的 URL 地址。
                        	 4. 微信公众号消息通知，平台：CSDN、主题：为文章标题、描述：为文章简述、跳转地址：从发布文章到CSDN获取 URL 地址
                        """)
                .defaultToolCallbacks(new SyncMcpToolCallbackProvider(sseMcpClient01(), sseMcpClient02()))
                .defaultAdvisors(
                        new PromptChatMemoryAdvisor(new InMemoryChatMemory()),
//                        new MessageChatMemoryAdvisor(new InMemoryChatMemory()),
                        new RagAnswerAdvisor(vectorStore, SearchRequest.builder()
                                .topK(5)
                                .filterExpression("knowledge == '知识库名称'")
                                .build()),
                        new SimpleLoggerAdvisor()
                )
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gpt-4.1")
                        .toolCallbacks(
                                FunctionToolCallback.builder("test",
                                                (Function<TestFunctionInput, String>) testFunctionInput -> {
                                                    log.info("函数请求:{}", testFunctionInput.getInput());
                                                    return "王大瓜今天入职啦!";
                                                })
                                        .description("王大瓜")
                                        .inputType(TestFunctionInput.class)
                                        .build())
                        .build())
                .build();
    }

    @Test
    public void test_chat_stream() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .baseUrl("https://apis.itedus.cn")
                        .apiKey("sk-lIqVNiHon00O6veJ15Cc57DaF5Dd401f93B3A107B4B3677e")
                        .completionsPath("v1/chat/completions")
                        .embeddingsPath("v1/embeddings")
                        .build())
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gpt-4.1")
                        .toolCallbacks(
                                FunctionToolCallback.builder("test",
                                                (Function<TestFunctionInput, String>) testFunctionInput -> {
                                                    log.info("函数请求:{}", testFunctionInput.getInput());
                                                    return "王大瓜今天入职啦!";
                                                })
                                        .description("王大瓜")
                                        .inputType(TestFunctionInput.class)
                                        .build())
//                        .toolCallbacks(new SyncMcpToolCallbackProvider(sseMcpClient01(), sseMcpClient02()).getToolCallbacks())
                        .toolCallbacks(new SyncMcpToolCallbackProvider(stdioMcpClient()).getToolCallbacks())
                        .build())
                .build();

        Flux<ChatResponse> stream = chatModel.stream(Prompt.builder()
                .messages(new UserMessage("""
                        	 在 /Users/fuzhengwei/Desktop 创建文件 file02.txt
                        """))
                .build());

        stream.subscribe(
                chatResponse -> {
                    AssistantMessage output = chatResponse.getResult().getOutput();
                    log.info("测试结果: {}", JSON.toJSONString(output));
                },
                Throwable::printStackTrace,
                () -> {
                    countDownLatch.countDown();
                    System.out.println("Stream completed");
                }
        );

        countDownLatch.await();
    }

    @Test
    public void test_client01() {
        ChatClient chatClient01 = ChatClient.builder(chatModel)
                .defaultSystem("""
                        你是一个专业的AI提示词优化专家。请帮我优化以下prompt，并按照以下格式返回：
                                                
                        # Role: [角色名称]
                                                
                        ## Profile
                        - language: [语言]
                        - description: [详细的角色描述]
                        - background: [角色背景]
                        - personality: [性格特征]
                        - expertise: [专业领域]
                        - target_audience: [目标用户群]
                                                
                        ## Skills
                                                
                        1. [核心技能类别]
                           - [具体技能]: [简要说明]
                           - [具体技能]: [简要说明]
                           - [具体技能]: [简要说明]
                           - [具体技能]: [简要说明]
                                                
                        2. [辅助技能类别]
                           - [具体技能]: [简要说明]
                           - [具体技能]: [简要说明]
                           - [具体技能]: [简要说明]
                           - [具体技能]: [简要说明]
                                                
                        ## Rules
                                                
                        1. [基本原则]：
                           - [具体规则]: [详细说明]
                           - [具体规则]: [详细说明]
                           - [具体规则]: [详细说明]
                           - [具体规则]: [详细说明]
                                                
                        2. [行为准则]：
                           - [具体规则]: [详细说明]
                           - [具体规则]: [详细说明]
                           - [具体规则]: [详细说明]
                           - [具体规则]: [详细说明]
                                                
                        3. [限制条件]：
                           - [具体限制]: [详细说明]
                           - [具体限制]: [详细说明]
                           - [具体限制]: [详细说明]
                           - [具体限制]: [详细说明]
                                                
                        ## Workflows
                                                
                        - 目标: [明确目标]
                        - 步骤 1: [详细说明]
                        - 步骤 2: [详细说明]
                        - 步骤 3: [详细说明]
                        - 预期结果: [说明]
                                                
                                                
                        ## Initialization
                        作为[角色名称]，你必须遵守上述Rules，按照Workflows执行任务。
                                                
                        请基于以上模板，优化并扩展以下prompt，确保内容专业、完整且结构清晰，注意不要携带任何引导词或解释，不要使用代码块包围。
                        """)
                .defaultAdvisors(
                        new PromptChatMemoryAdvisor(new InMemoryChatMemory()),
                        new RagAnswerAdvisor(vectorStore, SearchRequest.builder()
                                .topK(5)
                                .filterExpression("knowledge == '知识库名称'")
                                .build())
                )
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gpt-4.1")
                        .build())
                .build();

        String content = chatClient01
                .prompt("生成一篇文章")

                .system(s -> s.param("current_date", LocalDate.now().toString()))
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, "chatId-101")
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call().content();
        System.out.println("\n>>> ASSISTANT: " + content);

        ChatClient chatClient02 = ChatClient.builder(chatModel)
                .defaultSystem("""
                        	 你是一个 AI Agent 智能体，可以根据用户输入信息生成文章，并发送到 CSDN 平台以及完成微信公众号消息通知，今天是 {current_date}。
                                                
                        	 你擅长使用Planning模式，帮助用户生成质量更高的文章。
                                                
                        	 你的规划应该包括以下几个方面：
                        	 1. 分析用户输入的内容，生成技术文章。
                        	 2. 提取，文章标题（需要含带技术点）、文章内容、文章标签（多个用英文逗号隔开）、文章简述（100字）将以上内容发布文章到CSDN
                        	 3. 获取发送到 CSDN 文章的 URL 地址。
                        	 4. 微信公众号消息通知，平台：CSDN、主题：为文章标题、描述：为文章简述、跳转地址：为发布文章到CSDN获取 URL地址 CSDN文章链接 https 开头的地址。
                        """)
                .defaultTools(new SyncMcpToolCallbackProvider(sseMcpClient01(), sseMcpClient02()))
                .defaultAdvisors(
                        new PromptChatMemoryAdvisor(new InMemoryChatMemory()),
                        new SimpleLoggerAdvisor()
                )
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gpt-4.1")
                        .build())
                .build();

        String userInput = "生成一篇文章，要求如下 \r\n" + content;
        System.out.println("\n>>> QUESTION: " + userInput);
        System.out.println("\n>>> ASSISTANT: " + chatClient02
                .prompt(userInput)
                .system(s -> s.param("current_date", LocalDate.now().toString()))
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, "chatId-101")
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call().content());
    }

    @Test
    public void test_client02() {
        String userInput = "生成一篇文章";
        System.out.println("\n>>> QUESTION: " + userInput);
        System.out.println("\n>>> ASSISTANT: " + chatClient
                .prompt(userInput)
                .system(s -> s.param("current_date", LocalDate.now().toString()))
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, "chatId-101")
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call().content());
    }

    @Test
    public void test_clien03() {
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

    @Test
    public void test_weixin_notify() {
        String userInput01 = "获取王大瓜今年的年龄";
        System.out.println("\n>>> QUESTION: " + userInput01);
        String userMessage = chatClient
                .prompt(userInput01)
                .system(s -> s.param("current_date", LocalDate.now().toString()))
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, "chatId-101")
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call().content();
        System.out.println("\n>>> ASSISTANT: " + userMessage);

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(new UserMessage("发送微信公众号消息通知，平台：测试，主题：个人介绍，描述：" + userMessage + "，跳转地址：https://gaga.plus"));

        System.out.println("\n>>> ASSISTANT: " + chatClient
                .prompt(new Prompt(messages))
                .system(s -> s.param("current_date", LocalDate.now().toString()))
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, "chatId-101")
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call().content());
    }

    @Test
    public void test_rag() {
        String message = "王大瓜今年几岁";

        System.out.println("\n>>> QUESTION: " + message);
        System.out.println("\n>>> ASSISTANT: " +
                chatClient.prompt(message)
                        .system(s -> s.param("current_date", LocalDate.now().toString()))
                        .call().content());
    }

    public void uploadRag() {
        TikaDocumentReader reader = new TikaDocumentReader(resource);

        List<Document> documents = reader.get();
        List<Document> documentSplitterList = tokenTextSplitter.apply(documents);

        documents.forEach(doc -> doc.getMetadata().put("knowledge", "知识库名称"));
        documentSplitterList.forEach(doc -> doc.getMetadata().put("knowledge", "知识库名称"));

        vectorStore.accept(documentSplitterList);

        log.info("上传完成");
    }

    public McpSyncClient sseMcpClient01() {

        HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport.builder("http://192.168.1.109:8102").build();

        McpSyncClient mcpSyncClient = McpClient.sync(sseClientTransport).requestTimeout(Duration.ofMinutes(180)).build();

        var init = mcpSyncClient.initialize();
        System.out.println("SSE MCP Initialized: " + init);

        return mcpSyncClient;
    }

    public McpSyncClient sseMcpClient02() {

        HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport.builder("http://192.168.1.109:8101").build();

        McpSyncClient mcpSyncClient = McpClient.sync(sseClientTransport).requestTimeout(Duration.ofMinutes(180)).build();

        var init = mcpSyncClient.initialize();
        System.out.println("SSE MCP Initialized: " + init);

        return mcpSyncClient;
    }

    public McpSyncClient stdioMcpClient() {

        // based on
        // https://github.com/modelcontextprotocol/servers/tree/main/src/filesystem
        var stdioParams = ServerParameters.builder("npx")
                .args("-y", "@modelcontextprotocol/server-filesystem", "/Users/fuzhengwei/Desktop", "/Users/fuzhengwei/Desktop")
                .build();

        var mcpClient = McpClient.sync(new StdioClientTransport(stdioParams))
                .requestTimeout(Duration.ofSeconds(10)).build();

        var init = mcpClient.initialize();

        System.out.println("Stdio MCP Initialized: " + init);

        return mcpClient;

    }

    private static String getDbPath() {
        return Paths.get(System.getProperty("user.dir"), "target").toString();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestFunctionInput {

        @JsonProperty("input")
        private String input;

    }

    public static class LoggingAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

        @Override
        public String getName() {
            return "LoggingAdvisor";
        }

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
            System.out.println("\nRequest: " + advisedRequest);
            AdvisedResponse response = chain.nextAroundCall(advisedRequest);
            System.out.println("\nResponse: " + response);
            return response;
        }

        @Override
        public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
            System.out.println("\nRequest: " + advisedRequest);
            Flux<AdvisedResponse> responses = chain.nextAroundStream(advisedRequest);
            return new MessageAggregator().aggregateAdvisedResponse(responses, aggregatedAdvisedResponse -> {
                System.out.println("\nResponse: " + aggregatedAdvisedResponse);
            });
        }

    }


}
