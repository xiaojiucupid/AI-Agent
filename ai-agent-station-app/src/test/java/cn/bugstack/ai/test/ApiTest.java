package cn.bugstack.ai.test;

import cn.bugstack.ai.infrastructure.dao.IAiClientModelDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Autowired(required = false)
    private IAiClientModelDao aiClientModelConfigDao;

    @Test
    public void test() {
        log.info("测试完成");
    }

}
