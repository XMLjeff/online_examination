package com.project.online_examination.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/21 21:55
 * @description：
 * @modified By：
 * @version: $
 */
@Data
public class PaperVO implements Serializable {

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
}
