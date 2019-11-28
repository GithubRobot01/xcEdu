package com.xuecheng.manage_course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.netflix.discovery.converters.Auto;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    @Autowired
    CoursePicRepository coursePicRepository;
    @Autowired
    CmsPageClient cmsPageClient;

    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;

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
    public CoursePic findCoursePic(String id) {
        Optional<CoursePic> optional= coursePicRepository.findById(id);
        if (optional.isPresent()){
            CoursePic coursePic=optional.get();
            return coursePic;
        }
        return null;
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

    @Transactional
    public ResponseResult addCoursePic(String courseId, String pic) {
        CoursePic coursePic=null;
        //查询课程图片
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        if (optional.isPresent()){
            coursePic=optional.get();
        }
        if (coursePic==null){
            coursePic=new CoursePic();
        }
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {
        long count = coursePicRepository.deleteByCourseid(courseId);
        System.out.println(count);
        if (count>0){
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    //查询课程视图
    @Transactional
    public CourseView getCourseView(String id) {
        CourseView courseView=new CourseView();

        //查询课程基本信息
        Optional<CourseBase> optionalCourseBase = courseBaseRepository.findById(id);
        if (optionalCourseBase.isPresent()){
            courseView.setCourseBase(optionalCourseBase.get());
        }

        //查询课程图片
        Optional<CoursePic> optionalCoursePic = coursePicRepository.findById(id);
        if (optionalCoursePic.isPresent()){
            courseView.setCoursePic(optionalCoursePic.get());
        }

        //查询课程营销
        Optional<CourseMarket> optionalCourseMarket = courseMarketRepository.findById(id);
        if (optionalCourseMarket.isPresent()){
            courseView.setCourseMarket(optionalCourseMarket.get());
        }

        //课程计划信息
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        courseView.setTeachplanNode(teachplanNode);


        return courseView;
    }

    public CoursePublishResult preview(String id) {
        CourseBase courseBaseById = this.findCourseBaseById(id);
        CmsPage cmsPage=new CmsPage();
        cmsPage.setSiteId(publish_siteId);
        cmsPage.setDataUrl(publish_dataUrlPre+id);
        cmsPage.setPageName(id+".html");
        cmsPage.setPageAliase(courseBaseById.getName());
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        cmsPage.setPageWebPath(publish_page_webpath);
        cmsPage.setTemplateId(publish_templateId);
        //请求cms添加页面
        CmsPageResult cmsPageResult = cmsPageClient.saveCmsPage(cmsPage);
        if (!cmsPageResult.isSuccess()){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }

        CmsPage cmsPage1 = cmsPageResult.getCmsPage();
        String pageId = cmsPage1.getPageId();
        //拼装页面预览的url
        String url=previewUrl+pageId;
        //返回对象
        return new CoursePublishResult(CommonCode.SUCCESS,url);
    }
}
