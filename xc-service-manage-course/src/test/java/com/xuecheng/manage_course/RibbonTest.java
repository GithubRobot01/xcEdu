package com.xuecheng.manage_course;

import com.xuecheng.manage_course.client.CmsPageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RibbonTest {

    @Autowired
    RestTemplate restTemplate;

    @Test
    public void test(){
        String serviceName="XC-SERVICE-MANAGE-CMS";
        for (int i = 0; i < 10; i++) {
            //ribbon客户端从eurekaServer中获取服务列表
            ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://"+serviceName+"/cms/page/get/5a754adf6abb500ad05688d9", Map.class);
            Map map = forEntity.getBody();
            System.out.println(map);
        }

    }

}
