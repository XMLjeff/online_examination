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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/16 19:31
 * @description：管理员信息管理
 * @modified By：
 * @version: $
 */

@RestController
@RequestMapping("managerInfo")
@Api(tags = "管理员信息管理")
public class ManagerInfoController {

    @Autowired
    private IUserService userService;

    @ApiOperation(value = "新增管理员")
    @PostMapping("insertManager")
    @ApiOperationSupport(ignoreParameters = {"dto.majorIds", "dto.userId", "dto.userIds", "dto.pageNum", "dto.pageSize"})
    public ResultVO insertManager(@RequestBody UserDTO dto) {

        UserPO user = userService.getOne(Wrappers.lambdaQuery(UserPO.class).eq(UserPO::getUserName, dto.getUserName()));
        if (user != null) {
            return new ResultVO(MessageEnum.USER_EXIST);
        }

        UserPO userPO = UserConverter.INSTANCE.convertToPO(dto);
        userPO.setRole(UserConstant.ROLE_MANAGER);
        //密码md5加密
        userPO.setPassword(DigestUtils.md5DigestAsHex(userPO.getPassword().getBytes()));

        userService.save(userPO);

        return ResultVO.ok();
    }

    @ApiOperation(value = "删除管理员")
    @PostMapping("deleteManager")
    @ApiOperationSupport(includeParameters = {"dto.userIds"})
    public ResultVO deleteManager(@RequestBody UserDTO dto) {

        userService.removeByIds(dto.getUserIds());

        return ResultVO.ok();
    }

    @ApiOperation(value = "查询管理员")
    @PostMapping("queryManager")
    @ApiOperationSupport(includeParameters = {"dto.nickName", "dto.sex"})
    public ResultVO<List<UserPO>> queryManager(@RequestBody UserDTO dto) {

        List<UserPO> userPOS = userService.list(Wrappers.lambdaQuery(UserPO.class)
                .like(!StringUtils.isEmpty(dto.getNickName()), UserPO::getNickName, dto.getNickName())
                .eq(dto.getSex() != null, UserPO::getSex, dto.getSex())
                .eq(UserPO::getRole, UserConstant.ROLE_MANAGER));

        return ResultVO.ok().setData(userPOS);
    }

    @ApiOperation(value = "修改管理员")
    @PostMapping("editManager")
    @ApiOperationSupport(ignoreParameters = {"dto.userName", "dto.password", "dto.majorIds", "dto.userIds", "dto.pageNum", "dto.pageSize"})
    public ResultVO editManager(@RequestBody UserDTO dto) {

        UserPO userPO = UserConverter.INSTANCE.convertToPO(dto);
        userService.updateById(userPO);

        return ResultVO.ok();
    }
}
