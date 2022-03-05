package com.project.online_examination.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/19 23:08
 * @description：
 * @modified By：
 * @version: $
 */
@Data
public class ExamScoreVO {

    @ApiModelProperty(value = "课程名称")
    private String courseName;

    @ApiModelProperty(value = "成绩")
    private int score;
}
