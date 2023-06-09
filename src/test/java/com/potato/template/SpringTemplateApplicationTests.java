package com.potato.template;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

@SpringBootTest
class SpringTemplateApplicationTests {

    @Test
    void contextLoads() {
        String s = String.valueOf(new Random().nextInt(899999) + 100000);
        System.out.println(s);
    }

}
