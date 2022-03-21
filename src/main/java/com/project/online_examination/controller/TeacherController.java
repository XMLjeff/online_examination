package com.project.online_examination.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.project.online_examination.constant.UserConstant;
import com.project.online_examination.dto.TeacherDTO;
import com.project.online_examination.dto.UserDTO;
import com.project.online_examination.enums.MessageEnum;
import com.project.online_examination.mapstruct.UserConverter;
import com.project.online_examination.pojo.*;
import com.project.online_examination.service.ICourseService;
import com.project.online_examination.service.ITeacherAndCourseService;
import com.project.online_examination.service.IUserService;
import com.project.online_examination.vo.PageInfoVO;
import com.project.online_examination.vo.ResultVO;
import com.project.online_examination.vo.TeacherVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/3/20 21:40
 * @description：
 * @modified By：
 * @version: $
 */
@RestController
@RequestMapping("examineeInfo")
@Api(tags = "教师信息管理")
public class TeacherController {

    @Autowired
    private IUserService userService;
    @Autowired
    private ITeacherAndCourseService teacherAndCourseService;
    @Autowired
    private ICourseService courseService;

    @ApiOperation(value = "新增教师")
    @PostMapping("insertTeacher")
    @ApiOperationSupport(ignoreParameters = {"dto.userId", "dto.userIds", "dto.pageNum", "dto.pageSize", "dto.courseId"})
    public ResultVO insertTeacher(@RequestBody TeacherDTO dto) {

        UserPO user = userService.getOne(Wrappers.lambdaQuery(UserPO.class).eq(UserPO::getUserName, dto.getUserName()));
        if (user != null) {
            return new ResultVO(MessageEnum.USER_EXIST);
        }

        UserPO userPO = UserConverter.INSTANCE.convertToPO(dto);
        userPO.setRole(UserConstant.ROLE_TEACHER);
        //密码md5加密
        userPO.setPassword(DigestUtils.md5DigestAsHex(userPO.getPassword().getBytes()));

        userService.save(userPO);

        List<TeacherAndCoursePO> list = new ArrayList<>();
        String[] split = dto.getCourseIds().split(",");
        for (String s : split) {
            TeacherAndCoursePO teacherAndCoursePO = new TeacherAndCoursePO();
            teacherAndCoursePO.setUserId(userPO.getUserId());
            teacherAndCoursePO.setCourseId(Long.valueOf(s));
            list.add(teacherAndCoursePO);
        }

        teacherAndCourseService.saveBatch(list);

        return ResultVO.ok();
    }

    @ApiOperation(value = "删除教师")
    @PostMapping("deleteTeacher")
    @ApiOperationSupport(includeParameters = {"dto.userIds"})
    @Transactional
    public ResultVO deleteTeacher(@RequestBody TeacherDTO dto) {

        userService.removeByIds(dto.getUserIds());
        teacherAndCourseService.remove(Wrappers.lambdaQuery(TeacherAndCoursePO.class).in(TeacherAndCoursePO::getUserId, dto.getUserIds()));

        return ResultVO.ok();
    }

    @ApiOperation(value = "查询教师")
    @PostMapping("queryTeacher")
    @ApiOperationSupport(includeParameters = {"dto.nickName", "dto.sex", "dto.courseId", "dto.pageNum", "dto.pageSize"})
    public ResultVO<PageInfoVO<TeacherVO>> queryTeacher(@RequestBody TeacherDTO dto) {

        LambdaQueryWrapper<UserPO> wrapper = Wrappers.lambdaQuery(UserPO.class);
        wrapper.like(!StringUtils.isEmpty(dto.getNickName()), UserPO::getNickName, dto.getNickName())
                .eq(dto.getSex() != null, UserPO::getSex, dto.getSex())
                .eq(UserPO::getRole, UserConstant.ROLE_TEACHER);

        List<Long> userIds = null;
        if (dto.getCourseId() != null) {
            List<TeacherAndCoursePO> teacherAndCoursePOS = teacherAndCourseService.list(Wrappers.lambdaQuery(TeacherAndCoursePO.class).eq(TeacherAndCoursePO::getCourseId, dto.getCourseId()));
            if (!CollectionUtils.isEmpty(teacherAndCoursePOS)) {
                userIds = teacherAndCoursePOS.stream().map(t -> t.getUserId()).collect(Collectors.toList());
            } else {
                return ResultVO.ok().setData(new PageInfoVO<>(0L, null));
            }
        }

        if (!CollectionUtils.isEmpty(userIds)) {
            wrapper.in(UserPO::getUserId, userIds);
        }

        Map<Long, String> courseMap = courseService.list().stream().collect(Collectors.toMap(CoursePO::getCourseId, CoursePO::getCourseName));

        Page<UserPO> page = userService.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        List<UserPO> userPOS = page.getRecords();

        List<TeacherVO> teacherVOS = UserConverter.INSTANCE.convertToVO(userPOS);

        for (TeacherVO teacherVO : teacherVOS) {
            String courseName = teacherAndCourseService.list(Wrappers.lambdaQuery(TeacherAndCoursePO.class).eq(TeacherAndCoursePO::getUserId, teacherVO.getUserId()))
                    .stream().map(t -> courseMap.get(t.getCourseId())).collect(Collectors.joining(","));
            teacherVO.setCourseName(courseName);
        }

        return ResultVO.ok().setData(new PageInfoVO<>(page.getTotal(), teacherVOS));
    }

    @ApiOperation(value = "得到某个教师的所有课程")
    @PostMapping("getTeacherAllCourse")
    @ApiOperationSupport(includeParameters = {"dto.userId"})
    public ResultVO<List<TeacherAndCoursePO>> getTeacherAllCourse(TeacherDTO dto) {

        List<TeacherAndCoursePO> teacherAndCoursePOS = teacherAndCourseService.list(Wrappers.lambdaQuery(TeacherAndCoursePO.class).eq(TeacherAndCoursePO::getUserId, dto.getUserId()));

        return ResultVO.ok().setData(teacherAndCoursePOS);
    }

    @ApiOperation(value = "修改教师")
    @PostMapping("editTeacher")
    @ApiOperationSupport(ignoreParameters = {"dto.userName", "dto.password", "dto.userIds", "dto.pageNum", "dto.pageSize", "dto.courseId"})
    public ResultVO editTeacher(@RequestBody TeacherDTO dto) {

        UserPO userPO = UserConverter.INSTANCE.convertToPO(dto);
        userService.updateById(userPO);
        teacherAndCourseService.remove(Wrappers.lambdaQuery(TeacherAndCoursePO.class).eq(TeacherAndCoursePO::getUserId, dto.getUserId()));

        List<TeacherAndCoursePO> list = new ArrayList<>();
        String[] split = dto.getCourseIds().split(",");
        for (String s : split) {
            TeacherAndCoursePO teacherAndCoursePO = new TeacherAndCoursePO();
            teacherAndCoursePO.setUserId(userPO.getUserId());
            teacherAndCoursePO.setCourseId(Long.valueOf(s));
            list.add(teacherAndCoursePO);
        }

        teacherAndCourseService.saveBatch(list);

        return ResultVO.ok();
    }

}
