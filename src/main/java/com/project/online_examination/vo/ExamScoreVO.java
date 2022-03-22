package com.project.online_examination.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/19 23:08
 * @description：
 * @modified By：
 * @version: $
 */
@Data
public class ExamScoreVO implements Serializable {

    @ApiModelProperty(value = "课程名称")
    private String courseName;

    @ApiModelProperty(value = "最终成绩")
    private Integer finalScore;
}
