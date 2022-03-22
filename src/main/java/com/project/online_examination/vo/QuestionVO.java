package com.project.online_examination.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/3/21 22:38
 * @description：
 * @modified By：
 * @version: $
 */
@Data
public class QuestionVO implements Serializable {

    /**
     * 试题id
     */
    @ApiModelProperty(value = "试题id")
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
     * 试题类别，1：单选题；2：判断题；3：多选题；4：填空题；5：简答题
     */
    @ApiModelProperty(value = "试题类别，1：单选题；2：判断题；3：多选题；4：填空题；5：简答题")
    private Integer examinationQuestionsCategory;
    /**
     * 试题答案，多选题答案用，分隔；判断题：1：对；2：错
     */
    @ApiModelProperty(value = "试题答案，多选题答案用，分隔；判断题：1：对；2：错")
    private String examinationQuestionsAnswer;
    /**
     * 考生答案
     */
    @ApiModelProperty(value = "考生答案")
    private String examineeAnswer;
}
