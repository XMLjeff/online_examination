package com.project.online_examination.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/17 22:25
 * @description：
 * @modified By：
 * @version: $
 */
@Data
public class ScoreDTO implements Serializable {

    /**
     * 多个主键
     */
    @ApiModelProperty(value = "多个主键")
    private List<Long> examineeScoreIds;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long examineeScoreId;
    /**
     * 教师id
     */
    @ApiModelProperty(value = "教师id")
    private Long teacherId;
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
    /**
     * 填空题和简答题的成绩
     */
    @ApiModelProperty(value = "填空题和简答题的成绩")
    private Integer score;

    @ApiModelProperty(value = "考生昵称")
    private String nickName;

    @ApiModelProperty(value = "页码", example = "1", notes = "必填")
    private Long pageNum;

    @ApiModelProperty(value = "每页条数", example = "10", notes = "必填")
    private Long pageSize;
}
