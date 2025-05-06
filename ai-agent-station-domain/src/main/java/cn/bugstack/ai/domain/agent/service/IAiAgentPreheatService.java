package cn.bugstack.ai.domain.agent.service;

/**
 * AiAgent 装配服务接口
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-02 13:26
 */
public interface IAiAgentPreheatService {

    /**
     * 服务预热，启动时触达
     */
    void preheat() throws Exception;

    void preheat(Long aiClientId) throws Exception;

}
