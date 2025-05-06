package cn.bugstack.ai.trigger.http.admin;

import cn.bugstack.ai.infrastructure.dao.IAiClientToolMcpDao;
import cn.bugstack.ai.infrastructure.dao.po.AiClientToolMcp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MCP工具管理服务
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-06 16:01
 */
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/ai/admin/client/tool/mcp/")
public class AiAdminClientToolMcpController {

    @Resource
    private IAiClientToolMcpDao aiClientToolMcpDao;

    /**
     * 分页查询MCP工具列表
     *
     * @param aiClientToolMcp 查询条件
     * @return 分页结果
     */
    @RequestMapping(value = "queryMcpList", method = RequestMethod.POST)
    public ResponseEntity<List<AiClientToolMcp>> queryMcpList(@RequestBody AiClientToolMcp aiClientToolMcp) {
        try {
            List<AiClientToolMcp> mcpList = aiClientToolMcpDao.queryAllMcpConfig();
            return ResponseEntity.ok(mcpList);
        } catch (Exception e) {
            log.error("查询MCP工具列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 根据ID查询MCP工具
     *
     * @param id MCP工具ID
     * @return MCP工具
     */
    @RequestMapping(value = "queryMcpById", method = RequestMethod.GET)
    public ResponseEntity<AiClientToolMcp> queryMcpById(@RequestParam Long id) {
        try {
            AiClientToolMcp aiClientToolMcp = aiClientToolMcpDao.queryMcpConfigById(id);
            return ResponseEntity.ok(aiClientToolMcp);
        } catch (Exception e) {
            log.error("查询MCP工具异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 根据名称查询MCP工具
     *
     * @param mcpName MCP工具名称
     * @return MCP工具
     */
    @RequestMapping(value = "queryMcpByName", method = RequestMethod.GET)
    public ResponseEntity<AiClientToolMcp> queryMcpByName(@RequestParam String mcpName) {
        try {
            AiClientToolMcp aiClientToolMcp = aiClientToolMcpDao.queryMcpConfigByName(mcpName);
            return ResponseEntity.ok(aiClientToolMcp);
        } catch (Exception e) {
            log.error("根据名称查询MCP工具异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 新增MCP工具
     *
     * @param aiClientToolMcp MCP工具
     * @return 结果
     */
    @RequestMapping(value = "addMcp", method = RequestMethod.POST)
    public ResponseEntity<Boolean> addMcp(@RequestBody AiClientToolMcp aiClientToolMcp) {
        try {
            aiClientToolMcp.setCreateTime(LocalDateTime.now());
            aiClientToolMcp.setUpdateTime(LocalDateTime.now());
            int count = aiClientToolMcpDao.insert(aiClientToolMcp);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("新增MCP工具异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 更新MCP工具
     *
     * @param aiClientToolMcp MCP工具
     * @return 结果
     */
    @RequestMapping(value = "updateMcp", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateMcp(@RequestBody AiClientToolMcp aiClientToolMcp) {
        try {
            aiClientToolMcp.setUpdateTime(LocalDateTime.now());
            int count = aiClientToolMcpDao.update(aiClientToolMcp);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("更新MCP工具异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 删除MCP工具
     *
     * @param id MCP工具ID
     * @return 结果
     */
    @RequestMapping(value = "deleteMcp", method = RequestMethod.GET)
    public ResponseEntity<Boolean> deleteMcp(@RequestParam Long id) {
        try {
            int count = aiClientToolMcpDao.deleteById(id);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("删除MCP工具异常", e);
            return ResponseEntity.status(500).build();
        }
    }
}
