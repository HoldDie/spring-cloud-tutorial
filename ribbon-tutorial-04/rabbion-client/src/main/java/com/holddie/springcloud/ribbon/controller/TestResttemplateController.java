package com.holddie.springcloud.ribbon.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author HoldDie
 * @version 1.0.0
 * @email holddie@163.com
 * @date 2018/4/7 18:17
 */
@RestController
@RequestMapping("/resttemplate")
@Configuration
public class TestResttemplateController {
    static Logger logger = LoggerFactory.getLogger(TestResttemplateController.class);

    @Bean
    @LoadBalanced
        // 这个注解一定要加，不然LoadBalancerAutoConfiguration不会对它进行处理
    RestTemplate RestTemplate() {
        // ribbon feign是一个公司开源，很好的集成
        // resttemplate是spring的
        // 这是为了设置超时
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setReadTimeout(2000);
        simpleClientHttpRequestFactory.setConnectTimeout(2000);
        return new RestTemplate(simpleClientHttpRequestFactory);
    }

    @Autowired
    RestTemplate restTemplate; // spring内置,封装了http请求的工具类

    // properties
    @RequestMapping("/properties")
    public void properties() {
        // http://127.0.0.1:8080/sss
        // service-by-properties 通过名称 找到负载均衡器，负载均衡器会选择一个具体的实例
        String body = restTemplate.getForObject("http://service-by-properties/", String.class);
        testprint(body);// 仅仅是为了根据输出内容来判断是调用的哪个接口
    }

    // annotation
    @RequestMapping("/annotation")
    public void annotation() {
        String body = restTemplate.getForObject("http://service-by-annotation/", String.class);
        testprint(body);// 仅仅是为了根据输出内容来判断是调用的哪个接口
    }

    // eureka
    @RequestMapping("/eureka")
    public void eureka() {
        String body = restTemplate.getForObject("http://ribbon-sms-interface/sms", String.class);
        logger.warn("TestResttemplateController.eureka执行结果：{}", body);
    }

    // 仅仅是为了根据输出内容来判断是调用的哪个接口
    private void testprint(String body) {
        if (body.contains("dongnaoedu")) {
            logger.warn("根据负载均衡策略，选择实例:dongnaoedu.com");
        } else if (body.contains("csdn")) {
            logger.warn("根据负载均衡策略，选择实例:csdn.net");
        } else if (body.contains("baidu")) {
            logger.warn("根据负载均衡策略，选择实例:baidu.com");
        } else {
            logger.warn("火星....");
        }
    }
}
