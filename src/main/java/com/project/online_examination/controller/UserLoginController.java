package com.project.online_examination.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.project.online_examination.constant.UserConstant;
import com.project.online_examination.dto.UserDTO;
import com.project.online_examination.enums.MessageEnum;
import com.project.online_examination.mapstruct.UserConverter;
import com.project.online_examination.pojo.UserPO;
import com.project.online_examination.service.IUserService;
import com.project.online_examination.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/16 18:59
 * @description：用户登录
 * @modified By：
 * @version: $
 */

@RestController
@RequestMapping("userLogin")
@Api(tags = "用户登录")
public class UserLoginController {

    @Autowired
    private IUserService userService;

    @ApiOperation(value = "考生注册")
    @PostMapping("register")
    @ApiOperationSupport(ignoreParameters = {"dto.userId", "dto.userIds"})
    public ResultVO register(@RequestBody UserDTO dto) {

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

    @ApiOperation(value = "登录")
    @PostMapping("login")
    @ApiOperationSupport(includeParameters = {"dto.userName", "dto.password"})
    public ResultVO<UserPO> login(@RequestBody UserDTO dto) {

        UserPO user = userService.getOne(Wrappers.lambdaQuery(UserPO.class).eq(UserPO::getUserName, dto.getUserName()));
        if (user == null) {
            return new ResultVO(MessageEnum.USER_NOT_EXIST);
        }

        if (!user.getPassword().equals(DigestUtils.md5DigestAsHex(dto.getPassword().getBytes()))) {
            return new ResultVO(MessageEnum.ACCOUNT_PASSWORD_WRONG);
        }

        return ResultVO.ok().setData(user);
    }

    @ApiOperation(value = "修改密码")
    @PostMapping("updatePassword")
    @ApiOperationSupport(includeParameters = {"dto.userId", "dto.password"})
    public ResultVO updatePassword(@RequestBody UserDTO dto) {
        if (StringUtils.isEmpty(dto.getPassword())) {
            return new ResultVO(MessageEnum.PASSWORD_CANT_NULL);
        }

        UserPO userPO1 = new UserPO();
        userPO1.setUserId(dto.getUserId());
        userPO1.setPassword(DigestUtils.md5DigestAsHex(dto.getPassword().getBytes()));

        userService.updateById(userPO1);

        return ResultVO.ok();
    }

    @ApiOperation(value = "重置密码")
    @PostMapping("resetPassword")
    @ApiOperationSupport(includeParameters = {"dto.userName"})
    public ResultVO resetPassword(@RequestBody UserDTO dto) {

        UserPO one = userService.getOne(Wrappers.lambdaQuery(UserPO.class).eq(UserPO::getUserName, dto.getUserName()));
        if (one == null) {
            return new ResultVO(MessageEnum.USERNAME_NOT_EXIST);
        }

        userService.update(Wrappers.lambdaUpdate(UserPO.class)
                .eq(UserPO::getUserName, dto.getUserName())
                .set(UserPO::getPassword, DigestUtils.md5DigestAsHex(UserConstant.DEFAULT_PASSWORD.getBytes())));

        return new ResultVO(MessageEnum.DEFAULT_PASSWORD);
    }

    @ApiOperation(value = "退出登录")
    @PostMapping("quit")
    public ResultVO quit() {
        return ResultVO.ok();
    }
}
