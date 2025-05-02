package cn.bugstack.ai.domain.agent.model.entity;

import lombok.Data;

import java.util.List;

/**
 * 引擎启动器实体对象
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-02 13:33
 */
@Data
public class AiAgentEngineStarterEntity {

    private List<Long> clientIdList;

}
