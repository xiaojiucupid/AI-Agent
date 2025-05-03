package cn.bugstack.ai.test;

import cn.bugstack.ai.domain.agent.model.valobj.AiClientToolMcpVO;
import cn.bugstack.ai.infrastructure.dao.IAiClientModelDao;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Autowired(required = false)
    private IAiClientModelDao aiClientModelConfigDao;

    @Resource
    private ApplicationContext applicationContext;

    @Test
    public void test_applicationContext(){
        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();

    }

    @Test
    public void test() {
        log.info("测试完成");
    }

    @Test
    public void test_mcp_stdio() {
        AiClientToolMcpVO.TransportConfigStdio transportConfigStdio = JSON.parseObject("""
                "mcp-server-weixin": {
                  "command": "java",
                  "args": [
                    "-Dspring.ai.mcp.server.stdio=true",
                    "-jar",
                    "/Users/fuzhengwei/Applications/apache-maven-3.8.4/repository/cn/bugstack/mcp/mcp-server-weixin/1.0.0/mcp-server-weixin-1.0.0.jar"
                  ]
                }
                """, AiClientToolMcpVO.TransportConfigStdio.class);

        log.info("测试结果:{}", transportConfigStdio.getStdio());
    }

    @Test
    public void test_mcp_server_config() {
        String jsonStr = """
                {
                  "mcp-server-weixin": {
                    "command": "java",
                    "args": [
                      "-Dspring.ai.mcp.server.stdio=true",
                      "-jar",
                      "/Users/fuzhengwei/Applications/apache-maven-3.8.4/repository/cn/bugstack/mcp/mcp-server-weixin/1.0.0/mcp-server-weixin-1.0.0.jar"
                    ]
                  }
                }
                """;

        McpServerConfig mcpServerConfig = JSON.parseObject(jsonStr, McpServerConfig.class);
        log.info("解析结果: {}", JSON.toJSONString(mcpServerConfig));
    }

    @Data
    public static class McpServerConfig {

        private Map<String, ServerConfig> serverConfigMap;

    }

    @Data
    public static class ServerConfig {
        private String command;
        private List<String> args;
    }

    @Test
    public void test_universal_server_config() {
        String jsonStr = """
            {
              "mcp-server-weixin": {
                "command": "java",
                "args": [
                  "-Dspring.ai.mcp.server.stdio=true",
                  "-jar",
                  "/Users/fuzhengwei/Applications/apache-maven-3.8.4/repository/cn/bugstack/mcp/mcp-server-weixin/1.0.0/mcp-server-weixin-1.0.0.jar"
                ]
              }
            }
            """;

        UniversalServerConfig universalConfig = JSON.parseObject(jsonStr, UniversalServerConfig.class);
        log.info("所有配置: {}", universalConfig.getAllConfigs().keySet());

        // 获取特定配置
        ServerConfig weixinConfig = universalConfig.getConfig("mcp-server-weixin");
        if (weixinConfig != null) {
            log.info("微信服务器命令: {}", weixinConfig.getCommand());
        }

    }

    @Data
    public static class UniversalServerConfig {
        // 不声明具体字段，而是使用Map接收所有配置
        private java.util.Map<String, ServerConfig> allConfigs = new java.util.HashMap<>();

        // 自定义getter和setter
        public java.util.Map<String, ServerConfig> getAllConfigs() {
            return allConfigs;
        }

        public void setAllConfigs(java.util.Map<String, ServerConfig> allConfigs) {
            this.allConfigs = allConfigs;
        }

        // 便捷方法获取特定配置
        public ServerConfig getConfig(String name) {
            return allConfigs.get(name);
        }

        // FastJSON反序列化时会调用此方法
        public void put(String key, ServerConfig value) {
            allConfigs.put(key, value);
        }
    }

    @Test
    public void test_dynamic_config() {
        String jsonStr = """
        {
          "mcp-server-weixin": {
            "command": "java",
            "args": [
              "-Dspring.ai.mcp.server.stdio=true",
              "-jar",
              "/Users/fuzhengwei/Applications/apache-maven-3.8.4/repository/cn/bugstack/mcp/mcp-server-weixin/1.0.0/mcp-server-weixin-1.0.0.jar"
            ]
          },
          "mcp-server-csdn": {
            "command": "python",
            "args": [
              "-m",
              "server.py"
            ]
          }
        }
        """;

        // 直接使用 Map<String, ServerConfig> 接收所有配置
        Map<String, ServerConfig> allConfigs = JSON.parseObject(jsonStr,
                new com.alibaba.fastjson.TypeReference<>() {});

        // 遍历所有配置
        for (String configName : allConfigs.keySet()) {
            ServerConfig config = allConfigs.get(configName);
            log.info("配置名称: {}", configName);
            log.info("命令: {}", config.getCommand());
            log.info("参数列表: {}", config.getArgs());
            log.info("-------------------");
        }
    }
}
