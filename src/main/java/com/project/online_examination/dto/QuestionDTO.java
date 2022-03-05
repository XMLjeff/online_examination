package com.project.online_examination.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/17 21:53
 * @description：
 * @modified By：
 * @version: $
 */
@Data
public class QuestionDTO implements Serializable {

    /**
     * 多个试题id
     */
    @ApiModelProperty(value = "多个试题id")
    private List<Long> examinationQuestionsIds;
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

    @ApiModelProperty(value = "页码", example = "1", notes = "必填")
    private Long pageNum;

    @ApiModelProperty(value = "每页条数", example = "10", notes = "必填")
    private Long pageSize;
}
