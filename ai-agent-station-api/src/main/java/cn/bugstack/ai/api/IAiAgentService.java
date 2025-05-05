package cn.bugstack.ai.api;

import cn.bugstack.ai.api.response.Response;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * AiAgent 智能体对话服务接口
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-05 10:14
 */
public interface IAiAgentService {

    Response<String> chat(Long aiAgentId, String message);

    Response<Boolean> uploadRagFile(String name, String tag, List<MultipartFile> files);

}
