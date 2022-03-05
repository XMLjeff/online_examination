package com.project.online_examination.pojo;

import io.swagger.annotations.ApiModel;
import java.time.*;
import lombok.Data;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 课程表
 * </p>
 *
 * @author xmljeff
 * @since 2022-01-12
 */

@TableName("course")
@Data
public class CoursePO implements Serializable {
    private static final Long serialVersionUID = 1L;

/**
     * 课程id
     */
    @ApiModelProperty(value = "课程id")
    @TableId(type = IdType.AUTO)
    private Long courseId;
    /**
     * 课程名称
     */
    @ApiModelProperty(value = "课程名称")
    private String courseName;
    /**
     * 专业id，课程属于的专业,可能属于多个专业，用,分隔
     */
    @ApiModelProperty(value = "专业id，课程属于的专业,可能属于多个专业，用,分隔")
    private String majorIds;
    
}