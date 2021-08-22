package com.example;

import io.github.shuangbofu.helper.annotation.DaoScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by shuangbofu on 2021/8/22 1:52 下午
 */
@DaoScan(basePackages = "com.example.dao", mapperBasePackages = "com.example.dao.mapper")
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
