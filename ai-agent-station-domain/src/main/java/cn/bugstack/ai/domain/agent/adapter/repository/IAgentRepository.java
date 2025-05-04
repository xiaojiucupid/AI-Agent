package cn.bugstack.ai.domain.agent.adapter.repository;

import cn.bugstack.ai.domain.agent.model.valobj.AiClientAdvisorVO;
import cn.bugstack.ai.domain.agent.model.valobj.AiClientModelVO;
import cn.bugstack.ai.domain.agent.model.valobj.AiClientToolMcpVO;

import java.util.List;
import java.util.Map;

/**
 * 仓储服务
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-02 14:15
 */
public interface IAgentRepository {

    List<AiClientModelVO> queryAiClientModelVOListByClientIds(List<Long> clientIdList);

    List<AiClientToolMcpVO> queryAiClientToolMcpVOListByClientIds(List<Long> clientIdList);

    List<AiClientAdvisorVO> queryAdvisorConfigByClientIds(List<Long> clientIdList);

}
