package com.xuecheng.manage_course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    CourseBaseRepository courseBaseRepository;
    @Autowired
    TeachplanRepository teachplanRepository;
    @Autowired
    CourseMarketRepository courseMarketRepository;

    //查询课程计划
    @Transactional
    public TeachplanNode findTeachplanList(String courseId){
        return teachplanMapper.selectList(courseId);
    }

    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan) {
        if (teachplan==null|| StringUtils.isEmpty(teachplan.getPname())||StringUtils.isEmpty(teachplan.getCourseid())){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String parentid = teachplan.getParentid();
        String courseid = teachplan.getCourseid();
        if (StringUtils.isEmpty(parentid)){
            //如果父节点为空则获取根节点作为父节点
            parentid=getTeachplanRoot(courseid);
        }
        Optional<Teachplan> optional = teachplanRepository.findById(parentid);
        if (!optional.isPresent()){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //父节点
        Teachplan teachplanParent = optional.get();
        String parentGrade = teachplanParent.getGrade();
        //新节点
        Teachplan teachplanNew =new Teachplan();
        //将页面提交的teachplan信息拷贝到teachplanNew对象中
        BeanUtils.copyProperties(teachplan,teachplanNew);
        if (parentGrade.equals("1")){
            teachplanNew.setGrade("2");
        }else if (parentGrade.equals("2")){
            teachplanNew.setGrade("3");
        }

        teachplanNew.setParentid(parentid);
        teachplanNew.setStatus("0");
        teachplanRepository.save(teachplanNew);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    private String getTeachplanRoot(String courseid) {
        //校验根节点课程是否存在
        Optional<CourseBase> optional = courseBaseRepository.findById(courseid);
        if (!optional.isPresent()){
            return null;
        }
        CourseBase courseBase = optional.get();
        //取出课程计划根节点
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseid, "0");
        if (teachplanList==null||teachplanList.size()<=0){
            //新增一个根节点
            Teachplan teachplan=new Teachplan();
            teachplan.setCourseid(courseid);
            teachplan.setPname(courseBase.getName());
            teachplan.setParentid("0");
            teachplan.setGrade("1");
            teachplan.setStatus("0");
            teachplanRepository.save(teachplan);
            return teachplan.getId();
        }
        Teachplan teachplan=teachplanList.get(0);
        return teachplan.getId();
    }

    @Transactional
    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest) {
        if (page<=0){
            page=1;
        }
        if (size<=0){
            size=20;
        }
        PageHelper.startPage(page,size);
        Page<CourseInfo> pages= courseMapper.findAll();
        QueryResult<CourseInfo> queryResult=new QueryResult<>();
        queryResult.setTotal(pages.getTotal());
        queryResult.setList(pages.getResult());
        QueryResponseResult queryResponseResult=new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }

    @Transactional
    public AddCourseResult addCourseBase(CourseBase courseBase) {
        courseBase.setStatus("202001");
        courseBaseRepository.save(courseBase);
        return new AddCourseResult(CommonCode.SUCCESS,courseBase.getId());
    }

    public CourseBase findCourseBaseById(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    @Transactional
    public ResponseResult updateCourseBase(String id, CourseBase courseBase) {
        CourseBase course = this.findCourseBaseById(id);
        if (course==null){
            ExceptionCast.cast(CourseCode.COURSE_NOT_EXISTS);
        }
        course.setName(courseBase.getName());
        course.setMt(courseBase.getMt());
        course.setSt(courseBase.getSt());
        course.setGrade(courseBase.getGrade());
        course.setStudymodel(courseBase.getStudymodel());
        course.setUsers(courseBase.getUsers());
        course.setDescription(courseBase.getDescription());
        CourseBase save = courseBaseRepository.save(course);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Transactional
    public Map findCoursePicById(String id) {
        String pic = courseMapper.findCoursePicById(id);
        Map<String,String> map=new  HashMap<>();
        map.put("pic",pic);
        return map;
    }

    public CourseMarket getCourseMarketById(String courseId) {
        Optional<CourseMarket> optional = courseMarketRepository.findById(courseId);
        if (optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    public CourseMarket updateCourseMarket(String id, CourseMarket courseMarket) {
        CourseMarket courseMarket1 = this.getCourseMarketById(id);
        if (courseMarket1!=null){
            BeanUtils.copyProperties(courseMarket,courseMarket1);
            courseMarketRepository.save(courseMarket1);
        }else {
            courseMarket1=new CourseMarket();
            BeanUtils.copyProperties(courseMarket,courseMarket1);
            courseMarket1.setId(id);
            courseMarketRepository.save(courseMarket1);
        }
        return courseMarket1;
    }
}
