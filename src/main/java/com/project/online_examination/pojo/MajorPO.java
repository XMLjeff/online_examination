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
 * @since 2022-03-20
 */

@TableName("major")
@Data
public class MajorPO implements Serializable {
    private static final Long serialVersionUID = 1L;

/**
     * 专业id
     */
    @ApiModelProperty(value = "专业id")
    //@TableId(value = "major_id", type = IdType.ID_WORKER)
    //private Long id;//如果是Long类型的主键.则需要IdType.ID_WORKER;它会自动使用雪花算法生成不重复的ID.在新增的时候.自动赋值
    @TableId(type = IdType.AUTO)
    private Long majorId;
    /**
     * 专业名称
     */
    @ApiModelProperty(value = "专业名称")
    private String majorName;
    
}