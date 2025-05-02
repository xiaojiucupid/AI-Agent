package cn.bugstack.ai.domain.agent.service.armory.node;

import cn.bugstack.ai.domain.agent.model.entity.AiAgentEngineStarterEntity;
import cn.bugstack.ai.domain.agent.model.valobj.AiClientModelVO;
import cn.bugstack.ai.domain.agent.model.valobj.AiClientToolMcpVO;
import cn.bugstack.ai.domain.agent.service.armory.AbstractArmorySupport;
import cn.bugstack.ai.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import io.modelcontextprotocol.client.McpSyncClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Tool MCP Node
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-02 19:04
 */
@Slf4j
@Component
public class AiClientToolMcpNode extends AbstractArmorySupport {

    @Override
    protected String doApply(AiAgentEngineStarterEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("AiAgent 装配，tool mcp");

        List<AiClientToolMcpVO> aiClientToolMcpList = dynamicContext.getValue("aiClientToolMcpList");
        if (aiClientToolMcpList == null || aiClientToolMcpList.isEmpty()) {
            log.warn("没有可用的AI客户端工具配置 MCP");
            return null;
        }

        for (AiClientToolMcpVO mcpVO : aiClientToolMcpList) {
            // 构建Bean名称
            String beanName = "AiClientModel" + mcpVO.getId();

            // 创建OpenAiChatModel对象
            McpSyncClient mcpSyncClient = createMcpSyncClient(mcpVO);

            // 注册Bean
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(McpSyncClient.class, () -> mcpSyncClient);
            BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
            beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);

            // 如果Bean已存在，先移除
            if (beanFactory.containsBeanDefinition(beanName)) {
                beanFactory.removeBeanDefinition(beanName);
            }

            // 注册新的Bean
            beanFactory.registerBeanDefinition(beanName, beanDefinition);

            log.info("成功注册AI客户端模型Bean: {}", beanName);
        }

        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<AiAgentEngineStarterEntity, DefaultArmoryStrategyFactory.DynamicContext, String> get(AiAgentEngineStarterEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return null;
    }

}
