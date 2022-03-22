package com.project.online_examination.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/3/22 11:22
 * @description：
 * @modified By：
 * @version: $
 */
@Data
public class TeacherAndCourseVO {

    /**
     * 教师角色的用户id
     */
    @ApiModelProperty(value = "教师角色的用户id")
    private Long userId;
    /**
     * 课程id
     */
    @ApiModelProperty(value = "课程id")
    private Long courseId;
    /**
     * 课程名称
     */
    @ApiModelProperty(value = "课程名称")
    private String courseName;
}
