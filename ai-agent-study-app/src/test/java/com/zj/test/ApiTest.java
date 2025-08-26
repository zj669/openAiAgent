package com.zj.test;

import com.zj.infrastructure.dao.IAiClientToolMcpDao;
import com.zj.infrastructure.dao.po.AiClientToolMcp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {
    @Resource
    private IAiClientToolMcpDao aiClientToolMcpDao;

    @Test
    public void test() {
        AiClientToolMcp aiClientToolMcp = AiClientToolMcp.builder()
                .mcpId("test_001")
                .mcpName("更新后的测试MCP工具")
                .transportType("stdio")
                .transportConfig("{\"command\":\"npx\",\"args\":[\"-y\",\"test-mcp\"]}")
                .requestTimeout(300)
                .status(1)
                .updateTime(LocalDateTime.now())
                .build();

        int result = aiClientToolMcpDao.insert(aiClientToolMcp);
        log.info("更新结果: {}", result);
        log.info("测试完成");
    }

}
