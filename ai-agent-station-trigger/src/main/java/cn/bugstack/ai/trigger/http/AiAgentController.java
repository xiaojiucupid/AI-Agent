package cn.bugstack.ai.trigger.http;

import cn.bugstack.ai.api.IAiAgentService;
import cn.bugstack.ai.domain.agent.service.IAiAgentChatService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-05 10:15
 */
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/ai/agent/")
public class AiAgentController implements IAiAgentService {

    @Resource
    private IAiAgentChatService aiAgentChatService;

    /**
     * AI代理执行方法，用于处理用户输入的消息并返回AI代理的回复
     * 
     * 示例请求:
     * curl -X GET "http://localhost:8091/ai-agent-station/api/v1/ai/agent/exec?aiAgentId=1&message=生成一篇文章" -H "Content-Type: application/json"
     * 
     * @param aiAgentId AI代理ID，用于标识使用哪个AI代理
     * @param message 用户输入的消息内容
     * @return AI代理的回复内容
     */
    @RequestMapping(value = "exec", method = RequestMethod.GET)
    @Override
    public String exec(@RequestParam("aiAgentId") Long aiAgentId, @RequestParam("message") String message) {
        return aiAgentChatService.aiAgentChat(aiAgentId, message);
    }

}
