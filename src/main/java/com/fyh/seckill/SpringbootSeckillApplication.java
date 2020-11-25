package com.fyh.seckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.fyh.seckill.dao")
public class SpringbootSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootSeckillApplication.class, args);
    }

}
