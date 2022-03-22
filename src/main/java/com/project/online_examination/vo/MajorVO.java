package com.project.online_examination.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/21 21:56
 * @description：
 * @modified By：
 * @version: $
 */
@Data
public class MajorVO implements Serializable {

    /**
     * 专业id
     */
    @ApiModelProperty(value = "专业id")
    private Long majorId;
    /**
     * 专业名称
     */
    @ApiModelProperty(value = "专业名称")
    private String majorName;
}
