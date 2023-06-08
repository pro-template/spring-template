package com.potato.template.utils;


public enum HttpCodeEnum {
    OK(0, "OK"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败");
    /**
     * http返回码
     */
    private Integer code;
    /**
     * http返回信息
     */
    private String message;

    HttpCodeEnum(Integer code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Integer getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}
