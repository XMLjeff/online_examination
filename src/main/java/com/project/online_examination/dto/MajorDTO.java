package com.project.online_examination.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/16 20:15
 * @description：
 * @modified By：
 * @version: $
 */
@Data
public class MajorDTO implements Serializable {

    /**
     * 多个专业id
     */
    @ApiModelProperty(value = "多个专业id")
    private List<Long> majorIds;
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

    @ApiModelProperty(value = "页码", example = "1", notes = "必填")
    private Long pageNum;

    @ApiModelProperty(value = "每页条数", example = "10", notes = "必填")
    private Long pageSize;
}
