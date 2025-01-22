package com.openchowski.reactivespringfiles;

import org.springframework.boot.SpringApplication;

public class TestReactiveSpringFilesApplication {

    public static void main(String[] args) {
        SpringApplication.from(ReactiveSpringFilesApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
