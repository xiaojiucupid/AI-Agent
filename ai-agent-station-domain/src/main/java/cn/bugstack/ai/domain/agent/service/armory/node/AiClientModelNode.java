package cn.bugstack.ai.domain.agent.service.armory.node;

import cn.bugstack.ai.domain.agent.model.entity.AiAgentEngineStarterEntity;
import cn.bugstack.ai.domain.agent.model.valobj.AiClientModelVO;
import cn.bugstack.ai.domain.agent.service.armory.AbstractArmorySupport;
import cn.bugstack.ai.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AI客户端模型
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-02 14:25
 */
@Slf4j
@Component
public class AiClientModelNode extends AbstractArmorySupport {

    @Resource
    private AiClientToolMcpNode aiClientToolMcpNode;

    @Override
    protected String doApply(AiAgentEngineStarterEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("AiAgent 装配，客户端模型");

        List<AiClientModelVO> aiClientModelList = dynamicContext.getValue("aiClientModelList");
        
        if (aiClientModelList == null || aiClientModelList.isEmpty()) {
            log.warn("没有可用的AI客户端模型配置");
            return null;
        }
        
        // 遍历模型列表，为每个模型创建对应的Bean
        for (AiClientModelVO modelVO : aiClientModelList) {
            // 构建Bean名称
            String beanName = "AiClientModel" + modelVO.getId();
            
            // 创建OpenAiChatModel对象
            OpenAiChatModel chatModel = createOpenAiChatModel(modelVO);
            
            // 注册Bean
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(OpenAiChatModel.class, () -> chatModel);
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
        return aiClientToolMcpNode;
    }

}
