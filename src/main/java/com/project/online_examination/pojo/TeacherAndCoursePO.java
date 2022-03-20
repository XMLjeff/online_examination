package com.project.online_examination.pojo;

import io.swagger.annotations.ApiModel;
import java.time.*;
import lombok.Data;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 教师课程关联表
 * </p>
 *
 * @author xmljeff
 * @since 2022-03-20
 */

@TableName("teacher_and_course")
@Data
public class TeacherAndCoursePO implements Serializable {
    private static final Long serialVersionUID = 1L;

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
    
}