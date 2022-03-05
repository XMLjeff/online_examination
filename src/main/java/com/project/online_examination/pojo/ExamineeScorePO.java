package com.project.online_examination.pojo;

import io.swagger.annotations.ApiModel;
import java.time.*;
import lombok.Data;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 考生成绩表
 * </p>
 *
 * @author xmljeff
 * @since 2022-01-12
 */

@TableName("examinee_score")
@Data
public class ExamineeScorePO implements Serializable {
    private static final Long serialVersionUID = 1L;

/**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableId(type = IdType.AUTO)
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
     * 成绩
     */
    @ApiModelProperty(value = "成绩")
    private Integer score;
    
}