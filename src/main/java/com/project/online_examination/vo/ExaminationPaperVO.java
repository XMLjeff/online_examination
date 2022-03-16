package com.project.online_examination.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.project.online_examination.pojo.ExaminationPaperPO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/3/16 9:01
 * @description：
 * @modified By：
 * @version: $
 */
@Data
public class ExaminationPaperVO extends ExaminationPaperPO {

    /**
     * 专业名称
     */
    @ApiModelProperty(value = "专业名称")
    private String majorsName;
}
