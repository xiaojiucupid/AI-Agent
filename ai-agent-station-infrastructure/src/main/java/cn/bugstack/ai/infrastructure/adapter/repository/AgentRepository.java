package cn.bugstack.ai.infrastructure.adapter.repository;

import cn.bugstack.ai.domain.agent.adapter.repository.IAgentRepository;
import cn.bugstack.ai.domain.agent.model.valobj.AiClientAdvisorVO;
import cn.bugstack.ai.domain.agent.model.valobj.AiClientModelVO;
import cn.bugstack.ai.domain.agent.model.valobj.AiClientToolMcpVO;
import cn.bugstack.ai.infrastructure.dao.IAiClientAdvisorDao;
import cn.bugstack.ai.infrastructure.dao.IAiClientModelDao;
import cn.bugstack.ai.infrastructure.dao.IAiClientToolMcpDao;
import cn.bugstack.ai.infrastructure.dao.po.AiClientAdvisor;
import cn.bugstack.ai.infrastructure.dao.po.AiClientModel;
import cn.bugstack.ai.infrastructure.dao.po.AiClientToolMcp;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 仓储服务
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-02 17:14
 */
@Slf4j
@Repository
public class AgentRepository implements IAgentRepository {

    @Resource
    private IAiClientModelDao aiClientModelDao;

    @Resource
    private IAiClientToolMcpDao aiClientToolMcpDao;

    @Resource
    private IAiClientAdvisorDao aiClientAdvisorDao;

    @Override
    public List<AiClientModelVO> queryAiClientModelVOListByClientIds(List<Long> clientIdList) {
        // 根据客户端ID列表查询模型配置
        List<AiClientModel> aiClientModels = aiClientModelDao.queryModelConfigByClientIds(clientIdList);

        // 将PO对象转换为VO对象
        List<AiClientModelVO> aiClientModelVOList = new ArrayList<>();
        if (null != aiClientModels && !aiClientModels.isEmpty()) {
            for (AiClientModel aiClientModel : aiClientModels) {
                AiClientModelVO vo = new AiClientModelVO();
                vo.setId(aiClientModel.getId());
                vo.setModelName(aiClientModel.getModelName());
                vo.setBaseUrl(aiClientModel.getBaseUrl());
                vo.setApiKey(aiClientModel.getApiKey());
                vo.setCompletionsPath(aiClientModel.getCompletionsPath());
                vo.setEmbeddingsPath(aiClientModel.getEmbeddingsPath());
                vo.setModelType(aiClientModel.getModelType());
                vo.setModelVersion(aiClientModel.getModelVersion());
                vo.setTimeout(aiClientModel.getTimeout());
                aiClientModelVOList.add(vo);
            }
        }

        return aiClientModelVOList;
    }

    @Override
    public List<AiClientToolMcpVO> queryAiClientToolMcpVOListByClientIds(List<Long> clientIdList) {
        List<AiClientToolMcp> aiClientToolMcps = aiClientToolMcpDao.queryMcpConfigByClientIds(clientIdList);

        // 将PO对象转换为VO对象
        List<AiClientToolMcpVO> aiClientToolMcpVOList = new ArrayList<>();
        if (null != aiClientToolMcps && !aiClientToolMcps.isEmpty()) {
            for (AiClientToolMcp aiClientToolMcp : aiClientToolMcps) {
                AiClientToolMcpVO vo = new AiClientToolMcpVO();
                vo.setId(aiClientToolMcp.getId());
                vo.setMcpName(aiClientToolMcp.getMcpName());
                vo.setTransportType(aiClientToolMcp.getTransportType());
                vo.setRequestTimeout(aiClientToolMcp.getRequestTimeout());

                // 根据传输类型解析JSON配置
                String transportType = aiClientToolMcp.getTransportType();
                String transportConfig = aiClientToolMcp.getTransportConfig();

                try {
                    if ("sse".equals(transportType)) {
                        // 解析SSE配置
                        ObjectMapper objectMapper = new ObjectMapper();
                        AiClientToolMcpVO.TransportConfigSse sseConfig = objectMapper.readValue(transportConfig, AiClientToolMcpVO.TransportConfigSse.class);
                        vo.setTransportConfigSse(sseConfig);
                    } else if ("stdio".equals(transportType)) {
                        // 解析STDIO配置
                        Map<String, AiClientToolMcpVO.TransportConfigStdio.Stdio> stdio = JSON.parseObject(transportConfig,
                                new com.alibaba.fastjson.TypeReference<>() {
                                });
                        AiClientToolMcpVO.TransportConfigStdio stdioConfig = new AiClientToolMcpVO.TransportConfigStdio();
                        stdioConfig.setStdio(stdio);

                        vo.setTransportConfigStdio(stdioConfig);
                    }
                } catch (Exception e) {
                    log.error("解析传输配置失败: {}", e.getMessage(), e);
                }
                aiClientToolMcpVOList.add(vo);
            }
        }

        return aiClientToolMcpVOList;
    }

    @Override
    public List<AiClientAdvisorVO> queryAdvisorConfigByClientIds(List<Long> clientIdList) {
        List<AiClientAdvisor> aiClientAdvisors = aiClientAdvisorDao.queryAdvisorConfigByClientIds(clientIdList);
        
        if (null == aiClientAdvisors || aiClientAdvisors.isEmpty()) return Collections.emptyList();
        
        return aiClientAdvisors.stream().map(advisor -> {
            AiClientAdvisorVO vo = AiClientAdvisorVO.builder()
                    .id(advisor.getId())
                    .advisorName(advisor.getAdvisorName())
                    .advisorType(advisor.getAdvisorType())
                    .orderNum(advisor.getOrderNum())
                    .build();
            
            // 根据 advisorType 类型转换 extParam
            if (StringUtils.isNotEmpty(advisor.getExtParam())) {
                try {
                    if ("ChatMemory".equals(advisor.getAdvisorType())) {
                        AiClientAdvisorVO.ChatMemory chatMemory = JSON.parseObject(advisor.getExtParam(), AiClientAdvisorVO.ChatMemory.class);
                        vo.setChatMemory(chatMemory);
                    } else if ("RagAnswer".equals(advisor.getAdvisorType())) {
                        AiClientAdvisorVO.RagAnswer ragAnswer = JSON.parseObject(advisor.getExtParam(), AiClientAdvisorVO.RagAnswer.class);
                        vo.setRagAnswer(ragAnswer);
                    }
                } catch (Exception e) {
                    log.error("解析 extParam 失败，advisorId={}，extParam={}", advisor.getId(), advisor.getExtParam(), e);
                }
            }
            
            return vo;
        }).collect(Collectors.toList());
    }

}
