package io.github.zacoooo.weiboapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("io.github.zacoooo.weiboapi.mapper")
public class WeiboApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeiboApiApplication.class, args);
    }
}
