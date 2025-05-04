package cn.bugstack.ai.domain.agent.service.armory.node;

import cn.bugstack.ai.domain.agent.model.entity.AiAgentEngineStarterEntity;
import cn.bugstack.ai.domain.agent.model.valobj.AiClientAdvisorVO;
import cn.bugstack.ai.domain.agent.service.armory.AbstractArmorySupport;
import cn.bugstack.ai.domain.agent.service.armory.ext.RagAnswerAdvisor;
import cn.bugstack.ai.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
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
    private AiClientNode aiClientNode;

    @Resource
    private VectorStore vectorStore;

    @Override
    protected String doApply(AiAgentEngineStarterEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("AiAgent 装配，advisor");

        List<AiClientAdvisorVO> aiClientAdvisorList = dynamicContext.getValue("aiClientAdvisorList");
        if (aiClientAdvisorList == null || aiClientAdvisorList.isEmpty()) {
            log.warn("没有可用的AI客户端顾问（advisor）配置");
            return null;
        }

        for (AiClientAdvisorVO aiClientAdvisorVO : aiClientAdvisorList) {

            Advisor advisor = createAdvisor(aiClientAdvisorVO);

            registerBean(beanName(aiClientAdvisorVO.getId()), Advisor.class, advisor);

        }

        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<AiAgentEngineStarterEntity, DefaultArmoryStrategyFactory.DynamicContext, String> get(AiAgentEngineStarterEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return aiClientNode;
    }

    @Override
    protected String beanName(Long id) {
        return "AiClientAdvisor_" + id;
    }

    private Advisor createAdvisor(AiClientAdvisorVO aiClientAdvisorVO) {
        String advisorType = aiClientAdvisorVO.getAdvisorType();
        switch (advisorType) {
            case "ChatMemory" -> {
                return new PromptChatMemoryAdvisor(new InMemoryChatMemory());
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
