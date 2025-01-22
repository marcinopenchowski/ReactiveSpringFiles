package com.openchowski.reactivespringfiles;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.GenericContainer;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ReactiveSpringFilesApplicationTests {

    @Test
    void contextLoads() {
    }

}
