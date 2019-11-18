package com.xuecheng.api.cms;

import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "模板查询")
public interface CmsTemplateControllerApi {
    //页面查询
    @ApiOperation("查询所有模板名称")
    QueryResponseResult findAllTemplate();

}
