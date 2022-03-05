package com.project.online_examination.vo;

import com.project.online_examination.enums.MessageEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/16 17:19
 * @description：返回前端统一封装结构
 * @modified By：
 * @version: $
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResultVO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer status;

    private String msg;

    private T data;

    public ResultVO(MessageEnum msg) {
        this(msg.getCode(), msg.getMsg(), null);
    }

    public ResultVO(Integer status, T data, String msg) {
        this.status = status;
        this.data = data;
        this.msg = msg;
    }

    public ResultVO(Integer status, T data) {
        this.status = status;
        this.data = data;
    }

    /**
     * 构造成功执行的构造参数
     *
     * @return
     */
    public static ResultVO<Object> ok() {
        return new ResultVO<>(MessageEnum.OK);
    }

    /**
     * 内部服务器错误
     *
     * @return
     */
    public static ResultVO<Object> error() {
        return new ResultVO<>(MessageEnum.ERROR);
    }

    public ResultVO setData(T data) {
        this.data = data;
        return this;
    }
}
