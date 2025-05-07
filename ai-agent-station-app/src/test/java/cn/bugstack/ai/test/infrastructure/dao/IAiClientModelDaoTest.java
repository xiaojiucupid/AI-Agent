package cn.bugstack.ai.test.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.IAiClientModelDao;
import cn.bugstack.ai.infrastructure.dao.po.AiClientModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AI模型配置DAO层单元测试
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class IAiClientModelDaoTest {

    @Autowired(required = false)
    private IAiClientModelDao aiClientModelConfigDao;

    /**
     * 测试查询所有模型配置
     */
    @Test
    public void testQueryAllModelConfig() {
        // 执行查询
        List<AiClientModel> configList = aiClientModelConfigDao.queryAllModelConfig();

        // 验证结果不为空
        assertNotNull(configList);
        // 打印结果
        System.out.println("查询到" + configList.size() + "条模型配置记录");
        configList.forEach(System.out::println);
    }

    /**
     * 测试根据ID查询模型配置
     */
    @Test
    public void testQueryModelConfigById() {
        // 准备测试数据
        AiClientModel config = createTestModelConfig();
        aiClientModelConfigDao.insert(config);

        // 查询刚插入的记录
        AiClientModel result = aiClientModelConfigDao.queryModelConfigById(config.getId());

        // 验证结果
        assertNotNull(result);
        assertEquals(config.getModelName(), result.getModelName());
        assertEquals(config.getBaseUrl(), result.getBaseUrl());
        assertEquals(config.getApiKey(), result.getApiKey());
    }

    /**
     * 测试根据模型名称查询模型配置
     */
    @Test
    public void testQueryModelConfigByName() {
        // 准备测试数据
        AiClientModel config = createTestModelConfig();
        aiClientModelConfigDao.insert(config);

        // 查询刚插入的记录
        AiClientModel result = aiClientModelConfigDao.queryModelConfigByName(config.getModelName());

        // 验证结果
        assertNotNull(result);
        assertEquals(config.getId(), result.getId());
        assertEquals(config.getBaseUrl(), result.getBaseUrl());
        assertEquals(config.getApiKey(), result.getApiKey());
    }

    /**
     * 测试插入模型配置
     */
    @Test
    @Rollback
    public void testInsert() {
        // 准备测试数据
        AiClientModel config = createTestModelConfig();

        // 执行插入
        int rows = aiClientModelConfigDao.insert(config);

        // 验证结果
        assertEquals(1, rows);

        // 查询验证
        AiClientModel result = aiClientModelConfigDao.queryModelConfigById(config.getId());
    }

    /**
     * 测试更新模型配置
     */
    @Test
    @Rollback
    public void testUpdate() {
        // 准备测试数据
        AiClientModel config = createTestModelConfig();
        aiClientModelConfigDao.insert(config);

        // 修改数据
        String newModelName = "updated_model_" + System.currentTimeMillis();
        config.setModelName(newModelName);
        config.setBaseUrl("https://updated-api.example.com");
        config.setUpdateTime(new Date());

        // 执行更新
        int rows = aiClientModelConfigDao.update(config);

        // 验证结果
        assertEquals(1, rows);

        // 查询验证
        AiClientModel result = aiClientModelConfigDao.queryModelConfigById(config.getId());
        assertNotNull(result);
        assertEquals(newModelName, result.getModelName());
        assertEquals("https://updated-api.example.com", result.getBaseUrl());
    }

    /**
     * 测试删除模型配置
     */
    @Test
    @Rollback
    public void testDeleteById() {
        // 准备测试数据
        AiClientModel config = createTestModelConfig();
        aiClientModelConfigDao.insert(config);

        // 验证插入成功
        assertNotNull(aiClientModelConfigDao.queryModelConfigById(config.getId()));

        // 执行删除
        int rows = aiClientModelConfigDao.deleteById(config.getId());

        // 验证结果
        assertEquals(1, rows);

        // 查询验证
        assertNull(aiClientModelConfigDao.queryModelConfigById(config.getId()));
    }

    /**
     * 创建测试用的模型配置对象
     */
    private AiClientModel createTestModelConfig() {
        AiClientModel config = new AiClientModel();
        config.setModelName("test_model_" + System.currentTimeMillis());
        config.setBaseUrl("https://azure.itedus.cn");
        config.setApiKey("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");
        config.setCompletionsPath("v1/chat/completions");
        config.setEmbeddingsPath("v1/embeddings");
        config.setModelType("openai");
        config.setModelVersion("gpt-4.1-mini");
        config.setTimeout(30);
        config.setStatus(1);
        config.setCreateTime(new Date());
        config.setUpdateTime(new Date());
        return config;
    }
}