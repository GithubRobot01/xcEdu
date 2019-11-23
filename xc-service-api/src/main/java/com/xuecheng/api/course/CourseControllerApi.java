package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Api(value = "课程管理接口",description = "提供课程信息的增删改查")
public interface CourseControllerApi {
    @ApiOperation("课程信息查询")
    TeachplanNode findTeachplanList(String courseId);
    @ApiOperation("添加课程计划")
    ResponseResult addTeachplan(Teachplan teachplan);
    //查询课程列表
    @ApiOperation("查询我的课程列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页码",required = true,paramType = "path",dataType = "int"),
            @ApiImplicitParam(name = "size",value = "每页记录数",required = true,paramType = "path",dataType = "int")
    })
    QueryResponseResult findCourseList(
            int page,
            int size,
            CourseListRequest courseListRequest
    );
    @ApiOperation("添加基础课程")
    AddCourseResult addCourseBase(CourseBase courseBase);
    @ApiOperation("查询课程基本信息")
    CourseBase findCourseBaseById( String courseId);
    @ApiOperation("更新课程信息")
    ResponseResult updateCourseBase(String id,CourseBase courseBase);
    @ApiOperation("查询课程图片信息")
    Map findCoursePicById(String id);
}
