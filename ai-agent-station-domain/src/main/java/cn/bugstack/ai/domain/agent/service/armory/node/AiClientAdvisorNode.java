package cn.bugstack.ai.domain.agent.service.armory.node;

import cn.bugstack.ai.domain.agent.model.entity.AiAgentEngineStarterEntity;
import cn.bugstack.ai.domain.agent.model.valobj.AiClientAdvisorVO;
import cn.bugstack.ai.domain.agent.service.armory.AbstractArmorySupport;
import cn.bugstack.ai.domain.agent.service.armory.factory.element.RagAnswerAdvisor;
import cn.bugstack.ai.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AI 客户端顾问节点
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-04 08:44
 */
@Slf4j
@Component
public class AiClientAdvisorNode extends AbstractArmorySupport {

    @Resource
    private AiClientModelNode aiClientModelNode;

    @Resource
    private VectorStore vectorStore;

    @Override
    protected String doApply(AiAgentEngineStarterEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("Ai Agent 构建，advisor 顾问节点 {}", JSON.toJSONString(requestParameter));

        List<AiClientAdvisorVO> aiClientAdvisorList = dynamicContext.getValue("aiClientAdvisorList");
        if (aiClientAdvisorList == null || aiClientAdvisorList.isEmpty()) {
            log.warn("没有可用的AI客户端顾问（advisor）配置");
            return router(requestParameter, dynamicContext);
        }

        for (AiClientAdvisorVO aiClientAdvisorVO : aiClientAdvisorList) {
            // 构建顾问访问对象
            Advisor advisor = createAdvisor(aiClientAdvisorVO);
            // 注册Bean对象
            registerBean(beanName(aiClientAdvisorVO.getId()), Advisor.class, advisor);
        }

        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<AiAgentEngineStarterEntity, DefaultArmoryStrategyFactory.DynamicContext, String> get(AiAgentEngineStarterEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return aiClientModelNode;
    }

    @Override
    protected String beanName(Long id) {
        return "AiClientAdvisor_" + id;
    }

    private Advisor createAdvisor(AiClientAdvisorVO aiClientAdvisorVO) {
        String advisorType = aiClientAdvisorVO.getAdvisorType();
        switch (advisorType) {
            case "ChatMemory" -> {
                AiClientAdvisorVO.ChatMemory chatMemory = aiClientAdvisorVO.getChatMemory();
//                return new PromptChatMemoryAdvisor(new InMemoryChatMemory());
                return new PromptChatMemoryAdvisor(MessageWindowChatMemory.builder()
                        .maxMessages(chatMemory.getMaxMessages())
                        .build());
            }
            case "RagAnswer" -> {
                AiClientAdvisorVO.RagAnswer ragAnswer = aiClientAdvisorVO.getRagAnswer();
                return new RagAnswerAdvisor(vectorStore, SearchRequest.builder()
                        .topK(ragAnswer.getTopK())
                        .filterExpression(ragAnswer.getFilterExpression())
                        .build());
            }
        }

        throw new RuntimeException("err! advisorType " + advisorType + " not exist!");
    }

}
