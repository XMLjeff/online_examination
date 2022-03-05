package com.project.online_examination.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/17 20:56
 * @description：
 * @modified By：
 * @version: $
 */
@Data
public class PaperDTO implements Serializable {

    /**
     * 多个试卷id
     */
    @ApiModelProperty(value = "多个试卷id")
    private List<Long> examinationPaperIds;
    /**
     * 试卷id
     */
    @ApiModelProperty(value = "试卷id")
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

    @ApiModelProperty(value = "页码", example = "1", notes = "必填")
    private Long pageNum;

    @ApiModelProperty(value = "每页条数", example = "10", notes = "必填")
    private Long pageSize;
}
