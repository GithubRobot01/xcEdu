package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Test
    public void findAllTest(){
        List<CmsPage> list = cmsPageRepository.findAll();
        System.out.println(list);
    }

    //分页查询
    @Test
    public void findPageTest(){

        int page = 1; //从0开始
        int size = 10;
        Pageable pageable= PageRequest.of(page,size);
        Page<CmsPage> pages = cmsPageRepository.findAll(pageable);
        System.out.println(pages);
    }

    @Test
    public void updateTest(){
        //查询对象
        Optional<CmsPage> optional = cmsPageRepository.findById("5dca71e1e5ecbc115818b8d0");
        if (optional.isPresent()){
            CmsPage cmsPage=optional.get();
            cmsPage.setPageAliase("test001");
            cmsPage.setPageName("test");
            CmsPage save = cmsPageRepository.save(cmsPage);
            System.out.println(save);
        }

    }

    //自定义dao方法(根据页面名称查询)
    @Test
    public void findByPageNameTest(){
        CmsPage cmsPage = cmsPageRepository.findByPageName("test");
        System.out.println(cmsPage);
    }

    //自定义条件查询
    @Test
    public void findAllByExampleTest(){
        int page=0;
        int size=10;
        Pageable pageable=PageRequest.of(page,size);

        //条件值对象
        CmsPage cmsPage=new CmsPage();
        //cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
        cmsPage.setTemplateId("5ad9a24d68db5239b8fef199");
        //cmsPage.setPageAliase("轮播图");
        //条件匹配器
        ExampleMatcher exampleMatcher=ExampleMatcher.matching()
                .withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        Example<CmsPage> example=Example.of(cmsPage,exampleMatcher);

        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        List<CmsPage> content = all.getContent();
        System.out.println(content);
    }

}
