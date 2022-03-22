package com.project.online_examination.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/17 22:32
 * @description：
 * @modified By：
 * @version: $
 */
@Data
public class ScoreVO implements Serializable {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long examineeScoreId;
    /**
     * 考生id
     */
    @ApiModelProperty(value = "考生id")
    private Long userId;
    /**
     * 课程id
     */
    @ApiModelProperty(value = "课程id")
    private Long courseId;
    /**
     * 试卷id
     */
    @ApiModelProperty(value = "试卷id")
    private Long examinationPaperId;
    /**
     * 最终成绩
     */
    @ApiModelProperty(value = "最终成绩")
    private Integer finalScore;
    @ApiModelProperty(value = "考生昵称")
    private String nickName;
    @ApiModelProperty(value = "课程名称")
    private String courseName;
    @ApiModelProperty(value = "试卷名称")
    private String paperName;
    @ApiModelProperty(value = "专业名称")
    private String majorsName;
}
