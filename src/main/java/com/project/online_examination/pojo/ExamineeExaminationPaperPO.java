package com.project.online_examination.pojo;

import io.swagger.annotations.ApiModel;
import java.time.*;
import lombok.Data;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 考生试题表
 * </p>
 *
 * @author xmljeff
 * @since 2022-03-20
 */

@TableName("examinee_examination_paper")
@Data
public class ExamineeExaminationPaperPO implements Serializable {
    private static final Long serialVersionUID = 1L;

/**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    //@TableId(value = "examinee_examination_paper_id", type = IdType.ID_WORKER)
    //private Long id;//如果是Long类型的主键.则需要IdType.ID_WORKER;它会自动使用雪花算法生成不重复的ID.在新增的时候.自动赋值
    @TableId(type = IdType.AUTO)
    private Long examineeExaminationPaperId;
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