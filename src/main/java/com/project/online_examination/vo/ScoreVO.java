package com.project.online_examination.vo;

import com.project.online_examination.pojo.ExamineeScorePO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/17 22:32
 * @description：
 * @modified By：
 * @version: $
 */
@Data
public class ScoreVO extends ExamineeScorePO implements Serializable {

    @ApiModelProperty(value = "考生昵称")
    private String nickName;
    @ApiModelProperty(value = "课程名称")
    private String courseName;
    @ApiModelProperty(value = "试卷名称")
    private String paperName;
    @ApiModelProperty(value = "专业名称")
    private String majorsName;
}
