package cn.bugstack.ai.test.mcp;

import cn.bugstack.ai.domain.agent.model.valobj.AiClientToolMcpVO;
import cn.bugstack.ai.test.AiAgentTest;
import com.alibaba.fastjson.JSON;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.util.function.Function;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-10 08:11
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MCPTest {

    /**
     * ╔═════════════════════════════════════════════════════════════════════════╗
     * ║ Looks like Playwright Test or Playwright was just installed or updated. ║
     * ║ Please run the following command to download new browsers:              ║
     * ║                                                                         ║
     * ║     npx playwright install                                              ║
     * ║                                                                         ║
     * ║ <3 Playwright Team                                                      ║
     * ╚═════════════════════════════════════════════════════════════════════════╝
     */
    @Test
    public void test() {
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .baseUrl("https://apis.itedus.cn")
                        .apiKey("sk-lcuTPSSKTYSYQmrSE83a5694C38742B7Bd5f959106Ad6b85")
                        .completionsPath("v1/chat/completions")
                        .embeddingsPath("v1/embeddings")
                        .build())
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gpt-4.1")
//                        .toolCallbacks(new SyncMcpToolCallbackProvider(sseMcpClient()).getToolCallbacks())
                        .toolCallbacks(new SyncMcpToolCallbackProvider(sseMcpClient2auth()).getToolCallbacks())
                        .build())
                .build();

        ChatResponse call = chatModel.call(Prompt.builder().messages(new UserMessage("有哪些工具可以使用")).build());
        log.info("测试结果:{}", JSON.toJSONString(call.getResult()));
    }

    public McpSyncClient stdioMcpClient() {

        // https://github.com/jae-jae/fetcher-mcp
        var stdioParams = ServerParameters.builder("npx")
                .args("-y",
                        "fetcher-mcp")
                .build();

        var mcpClient = McpClient.sync(new StdioClientTransport(stdioParams))
                .requestTimeout(Duration.ofSeconds(50)).build();

        var init = mcpClient.initialize();

        System.out.println("Stdio MCP Initialized: " + init);

        return mcpClient;

    }

    /**
     * {
     * "baseUri":"http://127.0.0.1:9999/sse?apikey=DElk89iu8Ehhnbu"
     * }
     */
    // https://console.bce.baidu.com/ai_apaas/mcpServerCenter/mcp_server_appbuilder_ai_search/detail
    public McpSyncClient sseMcpClient2auth() {
        HttpClientSseClientTransport sseClientTransport;

        sseClientTransport = HttpClientSseClientTransport.builder("http://127.0.0.1:9999")
                .sseEndpoint("/sse?apikey=DElk89iu8Ehhnbu")
                .build();

//        sseClientTransport = HttpClientSseClientTransport.builder("http://192.168.1.108:8101")
//                .sseEndpoint("/sse")
//                .build();

//        sseClientTransport = HttpClientSseClientTransport.builder("http://127.0.0.1:9999")
//                .sseEndpoint("/sse")
//                .build();


        McpSyncClient mcpSyncClient = McpClient.sync(sseClientTransport).requestTimeout(Duration.ofMinutes(180)).build();
        var init_sse = mcpSyncClient.initialize();
        log.info("Tool SSE MCP Initialized {}", init_sse);
        return mcpSyncClient;
    }

    // https://console.bce.baidu.com/ai_apaas/mcpServerCenter/mcp_server_appbuilder_ai_search/detail
    public McpSyncClient sseMcpClient() {

        HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport.builder("http://appbuilder.baidu.com/v2/ai_search/mcp")
                .sseEndpoint("/sse?api_key=Bearer+bce-v3/ALTAK-3zODLb9qHozIftQlGwez5/2696e92781f***")
                .build();
        McpSyncClient mcpSyncClient = McpClient.sync(sseClientTransport).requestTimeout(Duration.ofMinutes(180)).build();
        var init_sse = mcpSyncClient.initialize();
        log.info("Tool SSE MCP Initialized {}", init_sse);
        return mcpSyncClient;
    }

    public McpSyncClient sseMcpClient2amap() {
        HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport
                .builder("https://mcp.amap.com")
                .sseEndpoint("/sse?key=801aabf79ed055c2ff78603cfe851787")
                .build();

        McpSyncClient mcpSyncClient = McpClient.sync(sseClientTransport).requestTimeout(Duration.ofMinutes(180)).build();
        var init_sse = mcpSyncClient.initialize();
        log.info("Tool SSE MCP Initialized {}", init_sse);

        return mcpSyncClient;
    }

}
