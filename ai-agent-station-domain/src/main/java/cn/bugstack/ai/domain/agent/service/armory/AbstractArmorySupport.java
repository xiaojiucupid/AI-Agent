package cn.bugstack.ai.domain.agent.service.armory;

import cn.bugstack.ai.domain.agent.adapter.repository.IAgentRepository;
import cn.bugstack.ai.domain.agent.model.entity.AiAgentEngineStarterEntity;
import cn.bugstack.ai.domain.agent.model.valobj.AiClientModelVO;
import cn.bugstack.ai.domain.agent.model.valobj.AiClientToolMcpVO;
import cn.bugstack.ai.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import cn.bugstack.wrench.design.framework.tree.AbstractMultiThreadStrategyRouter;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

/**
 * 生成器抽象类
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-02 13:23
 */
public abstract class AbstractArmorySupport extends AbstractMultiThreadStrategyRouter<AiAgentEngineStarterEntity, DefaultArmoryStrategyFactory.DynamicContext, String> {

    private final Logger log = LoggerFactory.getLogger(AbstractArmorySupport.class);

    @Resource
    protected ApplicationContext applicationContext;

    // 获取BeanFactory用于注册Bean
    protected DefaultListableBeanFactory beanFactory;// = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

    @Resource
    protected ThreadPoolExecutor threadPoolExecutor;

    @Resource
    protected IAgentRepository repository;

    @Override
    protected void multiThread(AiAgentEngineStarterEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws ExecutionException, InterruptedException, TimeoutException {
        // 缺省的
    }

    /**
     * 创建OpenAiChatModel对象
     *
     * @param modelVO 模型配置值对象
     * @return OpenAiChatModel实例
     */
    protected OpenAiChatModel createOpenAiChatModel(AiClientModelVO modelVO) {
        // 构建OpenAiApi
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(modelVO.getBaseUrl())
                .apiKey(modelVO.getApiKey())
                .completionsPath(modelVO.getCompletionsPath())
                .embeddingsPath(modelVO.getEmbeddingsPath())
                .build();

        // 构建OpenAiChatModel
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(modelVO.getModelName())
                        .build())
                .build();
    }

    protected McpSyncClient createMcpSyncClient(AiClientToolMcpVO aiClientToolMcpVO) {
        String transportType = aiClientToolMcpVO.getTransportType();

        switch (transportType) {
            case "sse" -> {
                AiClientToolMcpVO.TransportConfigSse transportConfigSse = aiClientToolMcpVO.getTransportConfigSse();
                HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport.builder(transportConfigSse.getBaseUri()).build();
                McpSyncClient mcpSyncClient = McpClient.sync(sseClientTransport).requestTimeout(Duration.ofMinutes(180)).build();
                var init_sse = mcpSyncClient.initialize();
                log.info("Tool SSE MCP Initialized {}", init_sse);
                return mcpSyncClient;
            }
            case "stdio" -> {
                AiClientToolMcpVO.TransportConfigStdio transportConfigStdio = aiClientToolMcpVO.getTransportConfigStdio();
                AiClientToolMcpVO.TransportConfigStdio.Stdio stdio = transportConfigStdio.getStdio();

                // https://github.com/modelcontextprotocol/servers/tree/main/src/filesystem
                var stdioParams = ServerParameters.builder(stdio.getCommand())
                        .args(stdio.getArgs())
                        .build();
                var mcpClient = McpClient.sync(new StdioClientTransport(stdioParams))
                        .requestTimeout(Duration.ofSeconds(10)).build();
                var init_stdio = mcpClient.initialize();
                log.info("Tool Stdio MCP Initialized {}", init_stdio);
                return mcpClient;
            }
        }

        throw new RuntimeException("err! transportType " + transportType + " not exist!");
    }

}
