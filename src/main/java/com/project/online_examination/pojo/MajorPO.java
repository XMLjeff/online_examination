package com.project.online_examination.pojo;

import io.swagger.annotations.ApiModel;
import java.time.*;
import lombok.Data;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 专业表
 * </p>
 *
 * @author xmljeff
 * @since 2022-01-12
 */

@TableName("major")
@Data
public class MajorPO implements Serializable {
    private static final Long serialVersionUID = 1L;

/**
     * 专业id
     */
    @ApiModelProperty(value = "专业id")
    @TableId(type = IdType.AUTO)
    private Long majorId;
    /**
     * 专业名称
     */
    @ApiModelProperty(value = "专业名称")
    private String majorName;
    
}