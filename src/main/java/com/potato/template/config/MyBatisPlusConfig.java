package com.potato.template.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.potato.template.mapper")
public class MyBatisPlusConfig {
}
