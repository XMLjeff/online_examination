package com.project.online_examination.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/3/20 22:05
 * @description：
 * @modified By：
 * @version: $
 */
@Data
public class TeacherVO implements Serializable {

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
     * 课程名称
     */
    @ApiModelProperty(value = "课程名称")
    private String courseName;
    /**
     * 电话号码
     */
    @ApiModelProperty(value = "电话号码")
    private String phoneNum;
}
