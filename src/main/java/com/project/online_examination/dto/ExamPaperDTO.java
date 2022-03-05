package com.project.online_examination.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/19 22:37
 * @description：
 * @modified By：
 * @version: $
 */
@Data
public class ExamPaperDTO implements Serializable {

    @ApiModelProperty(value = "主键")
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
     * 试题id
     */
    @ApiModelProperty(value = "试题id")
    private Long examinationQuestionsId;
    /**
     * 考生答案
     */
    @ApiModelProperty(value = "考生答案")
    private String examineeAnswer;
}
