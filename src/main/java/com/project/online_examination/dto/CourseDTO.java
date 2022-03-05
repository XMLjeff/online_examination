package com.project.online_examination.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/16 21:29
 * @description：
 * @modified By：
 * @version: $
 */
@Data
public class CourseDTO implements Serializable {

    /**
     * 多个课程id
     */
    @ApiModelProperty(value = "多个课程id")
    private List<Long> courseIds;
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
    /**
     * 专业id，课程属于的专业,可能属于多个专业，用,分隔
     */
    @ApiModelProperty(value = "专业id，课程属于的专业,可能属于多个专业，用,分隔")
    private String majorIds;

    @ApiModelProperty(value = "页码", example = "1", notes = "必填")
    private Long pageNum;

    @ApiModelProperty(value = "每页条数", example = "10", notes = "必填")
    private Long pageSize;
}
