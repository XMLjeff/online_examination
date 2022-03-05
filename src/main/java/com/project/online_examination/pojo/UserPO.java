package com.project.online_examination.pojo;

import io.swagger.annotations.ApiModel;

import java.time.*;

import lombok.Data;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author xmljeff
 * @since 2022-01-12
 */

@TableName("user")
@Data
public class UserPO implements Serializable {
    private static final Long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableId(type = IdType.AUTO)
    private Long userId;
    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String userName;
    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String password;
    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称")
    private String nickName;
    /**
     * 性别：0：男；1：女，2：未知
     */
    @ApiModelProperty(value = "性别：0：男；1：女，2：未知")
    private Integer sex;
    /**
     * 角色：0：admin;1:管理员；2：考生
     */
    @ApiModelProperty(value = "角色：0：admin;1:管理员；2：考生")
    private Integer role;
    /**
     * 专业id，学生属于哪个专业,可能是多个，用，分隔
     */
    @ApiModelProperty(value = "专业id，学生属于哪个专业,可能是多个，用，分隔")
    private String majorIds;
    /**
     * 电话号码
     */
    @ApiModelProperty(value = "电话号码")
    private String phoneNum;

}