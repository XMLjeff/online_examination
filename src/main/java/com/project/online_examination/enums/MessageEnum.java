package com.project.online_examination.enums;

import com.project.online_examination.constant.Constant;
import com.project.online_examination.constant.UserConstant;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/16 17:16
 * @description：返回的提示内容
 * @modified By：
 * @version: $
 */
public enum MessageEnum {

    OK(1000, "成功"),
    ACCOUNT_PASSWORD_WRONG(1001, "账号或密码错误"),
    USER_EXIST(1002, "用户已存在"),
    USER_NOT_EXIST(1003, "用户不存在，请先注册"),
    MAJOR_EXIST(1004, "专业已存在"),
    COURSE_EXIST(1005, "课程已存在"),
    PAPER_EXIST(1006, "试卷已存在"),
    QUESTION_EXIST(1007, "试题已存在"),
    PASSWORD_CANT_NULL(1008, "密码不能为空"),
    USERNAME_NOT_EXIST(1009, "用户不存在"),
    DEFAULT_PASSWORD(1010, "重置成功,重置之后的密码是" + UserConstant.DEFAULT_PASSWORD),
    FILE_EXCEPTION(1011, Constant.FILE_EXCEPTION),


    ERROR(2000, "内部服务器错误。");

    private final String msg;
    private final Integer code;

    MessageEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }

    public Integer getCode() {
        return this.code;
    }
}
