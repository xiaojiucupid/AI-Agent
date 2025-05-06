package cn.bugstack.ai.domain.agent.service.preheat;

import cn.bugstack.ai.domain.agent.adapter.repository.IAgentRepository;
import cn.bugstack.ai.domain.agent.model.entity.AiAgentEngineStarterEntity;
import cn.bugstack.ai.domain.agent.service.IAiAgentPreheatService;
import cn.bugstack.ai.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 装配服务
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-05 09:12
 */
@Slf4j
@Service
public class AiAgentPreheatService implements IAiAgentPreheatService {

    @Resource
    private DefaultArmoryStrategyFactory defaultArmoryStrategyFactory;
    @Resource
    private IAgentRepository repository;

    @Override
    public void preheat() throws Exception {
        List<Long> aiClientIds = repository.queryAiClientIds();
        StrategyHandler<AiAgentEngineStarterEntity, DefaultArmoryStrategyFactory.DynamicContext, String> handler = defaultArmoryStrategyFactory.strategyHandler();
        handler.apply(AiAgentEngineStarterEntity.builder()
                .clientIdList(aiClientIds)
                .build(), new DefaultArmoryStrategyFactory.DynamicContext());
    }

    @Override
    public void preheat(Long aiClientId) throws Exception {
        StrategyHandler<AiAgentEngineStarterEntity, DefaultArmoryStrategyFactory.DynamicContext, String> handler = defaultArmoryStrategyFactory.strategyHandler();
        handler.apply(AiAgentEngineStarterEntity.builder()
                .clientIdList(Collections.singletonList(aiClientId))
                .build(), new DefaultArmoryStrategyFactory.DynamicContext());
    }

}
