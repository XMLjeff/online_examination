package com.project.online_examination.pojo;

import io.swagger.annotations.ApiModel;

import java.time.*;

import lombok.Data;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 试卷表
 * </p>
 *
 * @author xmljeff
 * @since 2022-01-12
 */

@TableName("examination_paper")
@Data
public class ExaminationPaperPO implements Serializable {
    private static final Long serialVersionUID = 1L;

    /**
     * 试卷id
     */
    @ApiModelProperty(value = "试卷id")
    @TableId(type = IdType.AUTO)
    private Long examinationPaperId;
    /**
     * 试卷名称
     */
    @ApiModelProperty(value = "试卷名称")
    private String examinationPaperName;
    /**
     * 课程id，试卷属于哪个课程
     */
    @ApiModelProperty(value = "课程id，试卷属于哪个课程")
    private Long courseId;

}