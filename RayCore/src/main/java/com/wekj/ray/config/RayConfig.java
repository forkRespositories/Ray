package com.wekj.ray.config;

import com.wekj.ray.generator.IdGenerator;
import com.star.meta.*;
import com.wekj.ray.meta.*;
import com.wekj.ray.properties.RayRedisProperties;
import com.wekj.ray.spring.SpringContextHolder;
import com.wekj.ray.support.redis.RedisConnectionContainer;
import com.wekj.ray.util.StringUtil;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * @author liuna
 */
@Configuration
public class RayConfig {

    @Bean
    @ConfigurationProperties(prefix = "ray.redis")
    public RayRedisProperties rayRedisProperties() {
        return new RayRedisProperties();
    }

    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }

    @Bean
    public RedisConnectionContainer redisConnectionContainer(RayRedisProperties rayRedisProperties) {
        RedisURI.Builder builder = RedisURI.builder()
                .withHost(rayRedisProperties.getHost())
                .withPort(rayRedisProperties.getPort())
                .withDatabase(rayRedisProperties.getDb())
                .withTimeout(Duration.ofSeconds(30));
        if (StringUtil.isNotEmpty(rayRedisProperties.getPassword())) {
            builder.withPassword(rayRedisProperties.getPassword());
        }
        RedisURI redisURI = builder.build();
        RedisClient redisClient = RedisClient.create(redisURI);
        StatefulRedisConnection<String, String> connect = redisClient.connect();
        RedisConnectionContainer redisConnectionContainer = new RedisConnectionContainer();
        redisConnectionContainer.setConnection(connect);
        return redisConnectionContainer;
    }

    @Bean
    public MachineIdFactory machineIdFactory() {
        return new RedisMachineIdFactory();
    }

    @Bean
    public InitTimeStampFactory initTimeStampFactory() {
        return new RedisInitTimeStampFactory();
    }

    @Bean
    public TimeAndSequences timeAndSequences(InitTimeStampFactory initTimeStampFactory) {
        return new TimeAndSequences(initTimeStampFactory);
    }

    @Bean
    public IdGenerator idGenerator(TimeAndSequences timeAndSequences, MachineIdFactory machineIdFactory) {
        return new IdGenerator(machineIdFactory, timeAndSequences);
    }

}