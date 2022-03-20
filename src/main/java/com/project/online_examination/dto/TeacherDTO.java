package com.project.online_examination.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/3/20 21:41
 * @description：
 * @modified By：
 * @version: $
 */
@Data
public class TeacherDTO implements Serializable {

    /**
     * 多个主键
     */
    @ApiModelProperty(value = "多个主键")
    private List<Long> userIds;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
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
     * 课程ids,用,分隔
     */
    @ApiModelProperty(value = "课程ids,用,分隔")
    private String courseIds;
    /**
     * 课程id
     */
    @ApiModelProperty(value = "课程id")
    private Long courseId;
    /**
     * 电话号码
     */
    @ApiModelProperty(value = "电话号码")
    private String phoneNum;

    @ApiModelProperty(value = "页码", example = "1", notes = "必填")
    private Long pageNum;

    @ApiModelProperty(value = "每页条数", example = "10", notes = "必填")
    private Long pageSize;
}
