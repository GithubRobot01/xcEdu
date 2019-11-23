package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import io.swagger.annotations.Api;

@Api(value = "课程分类管理接口")
public interface CategoryControllerApi {
   CategoryNode findCategory();
}
