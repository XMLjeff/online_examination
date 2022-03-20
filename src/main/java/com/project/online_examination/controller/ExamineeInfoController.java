package com.project.online_examination.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.project.online_examination.constant.UserConstant;
import com.project.online_examination.dto.UserDTO;
import com.project.online_examination.enums.MessageEnum;
import com.project.online_examination.mapstruct.UserConverter;
import com.project.online_examination.pojo.ExamineeExaminationPaperPO;
import com.project.online_examination.pojo.ExamineeScorePO;
import com.project.online_examination.pojo.UserPO;
import com.project.online_examination.service.IExamineeExaminationPaperService;
import com.project.online_examination.service.IExamineeScoreService;
import com.project.online_examination.service.IUserService;
import com.project.online_examination.vo.PageInfoVO;
import com.project.online_examination.vo.ResultVO;
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

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/16 19:57
 * @description：考生信息管理
 * @modified By：
 * @version: $
 */

@RestController
@RequestMapping("examineeInfo")
@Api(tags = "考生信息管理")
public class ExamineeInfoController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IExamineeExaminationPaperService examineeExaminationPaperService;
    @Autowired
    private IExamineeScoreService examineeScoreService;

    @ApiOperation(value = "新增考生")
    @PostMapping("insertExaminee")
    @ApiOperationSupport(ignoreParameters = {"dto.userId", "dto.userIds", "dto.pageNum", "dto.pageSize"})
    public ResultVO insertExaminee(@RequestBody UserDTO dto) {

        UserPO user = userService.getOne(Wrappers.lambdaQuery(UserPO.class).eq(UserPO::getUserName, dto.getUserName()));
        if (user != null) {
            return new ResultVO(MessageEnum.USER_EXIST);
        }

        UserPO userPO = UserConverter.INSTANCE.convertToPO(dto);
        userPO.setRole(UserConstant.ROLE_EXAMINEE);
        //密码md5加密
        userPO.setPassword(DigestUtils.md5DigestAsHex(userPO.getPassword().getBytes()));

        userService.save(userPO);

        return ResultVO.ok();
    }

    @ApiOperation(value = "删除考生")
    @PostMapping("deleteExaminee")
    @ApiOperationSupport(includeParameters = {"dto.userIds"})
    @Transactional
    public ResultVO deleteExaminee(@RequestBody UserDTO dto) {

        for (Long userId : dto.getUserIds()) {
            //删除考生信息
            userService.removeById(userId);
            //删除考生试题信息
            examineeExaminationPaperService.remove(Wrappers.lambdaQuery(ExamineeExaminationPaperPO.class)
                    .eq(ExamineeExaminationPaperPO::getUserId, userId));
            //删除考生成绩信息
            examineeScoreService.remove(Wrappers.lambdaQuery(ExamineeScorePO.class)
                    .eq(ExamineeScorePO::getUserId, userId));
        }

        return ResultVO.ok();
    }

    @ApiOperation(value = "查询考生")
    @PostMapping("queryExaminee")
    @ApiOperationSupport(includeParameters = {"dto.nickName", "dto.sex", "dto.majorIds", "dto.pageNum", "dto.pageSize"})
    public ResultVO<PageInfoVO<UserPO>> queryExaminee(@RequestBody UserDTO dto) {

        LambdaQueryWrapper<UserPO> wrapper = Wrappers.lambdaQuery(UserPO.class);
        wrapper.like(!StringUtils.isEmpty(dto.getNickName()), UserPO::getNickName, dto.getNickName())
                .eq(dto.getSex() != null, UserPO::getSex, dto.getSex())
                .eq(UserPO::getRole, UserConstant.ROLE_EXAMINEE);

        if (!StringUtils.isEmpty(dto.getMajorIds())) {
            String[] split = dto.getMajorIds().split(",");
            for (String s : split) {
                wrapper.apply("find_in_set({0},major_ids)", s);
            }
        }

        Page<UserPO> page = userService.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);

        return ResultVO.ok().setData(new PageInfoVO<>(page.getTotal(), page.getRecords()));

    }

    @ApiOperation(value = "修改考生")
    @PostMapping("editExaminee")
    @ApiOperationSupport(ignoreParameters = {"dto.userName", "dto.password", "dto.userIds", "dto.pageNum", "dto.pageSize"})
    public ResultVO editExaminee(@RequestBody UserDTO dto) {

        UserPO userPO = UserConverter.INSTANCE.convertToPO(dto);
        userService.updateById(userPO);

        return ResultVO.ok();
    }
}
