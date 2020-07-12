package com.maxilect.idfactory;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RedisConfig {

    @Bean
    RedissonClient redisConfiguration(@Value("${redis.server}") String server
            , @Value("${redis.port}") String port) {

        if (server == null || port == null || server.isEmpty() || port.isEmpty()) {
            throw new BeanCreationException("Config parameters redis.server and redis.port are absent in configuration file");
        }

        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + server + ":" + port);
        return Redisson.create(config);
    }

}
