package com.github.lihang941.vertx.test;

import com.github.lihang941.common.logger.LoggerBeanPostProcessor;
import io.vertx.core.Vertx;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : lihang1329@gmail.com
 * @since : 2018/11/20
 */
@Configuration
public class Config {

    @Bean
    public Vertx vertx (){
        return Vertx.vertx();
    }


    @Bean
    public LoggerBeanPostProcessor loggerBeanPostProcessor(){
        return new LoggerBeanPostProcessor();
    }

}
