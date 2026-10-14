package org.example.agoracomments;

import org.example.agoracomments.configuration.RabbitMqConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class AgoraCommentsApplicationTests {

    @MockitoBean
    private RabbitMqConfig rabbitMqConfig;

    @Test
    void contextLoads() {
    }

}
