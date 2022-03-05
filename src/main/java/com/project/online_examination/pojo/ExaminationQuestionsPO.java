package com.project.online_examination.pojo;

import io.swagger.annotations.ApiModel;
import java.time.*;
import lombok.Data;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 试题表
 * </p>
 *
 * @author xmljeff
 * @since 2022-01-12
 */

@TableName("examination_questions")
@Data
public class ExaminationQuestionsPO implements Serializable {
    private static final Long serialVersionUID = 1L;

/**
     * 试题id
     */
    @ApiModelProperty(value = "试题id")
    @TableId(type = IdType.AUTO)
    private Long examinationQuestionsId;
    /**
     * 试题名称
     */
    @ApiModelProperty(value = "试题名称")
    private String examinationQuestionsName;
    /**
     * 试题分数
     */
    @ApiModelProperty(value = "试题分数")
    private Integer examinationQuestionsScore;
    /**
     * 试题类别，1：单选题；2：判断题；3：多选题；4：填空题
     */
    @ApiModelProperty(value = "试题类别，1：单选题；2：判断题；3：多选题；4：填空题")
    private Integer examinationQuestionsCategory;
    /**
     * 试题答案，多选题答案用，分隔；判断题：1：对；2：错
     */
    @ApiModelProperty(value = "试题答案，多选题答案用，分隔；判断题：1：对；2：错")
    private String examinationQuestionsAnswer;
    /**
     * 试卷ids，题目属于哪个试卷，多个用，分隔
     */
    @ApiModelProperty(value = "试卷ids，题目属于哪个试卷，多个用，分隔")
    private String examinationPaperIds;
    
}