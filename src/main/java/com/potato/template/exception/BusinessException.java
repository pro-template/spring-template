package com.potato.template.exception;

import com.potato.template.utils.HttpCodeEnum;

/**
 * 自定义异常
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(HttpCodeEnum httpCodeEnum) {
        super(httpCodeEnum.getMessage());
        this.code = httpCodeEnum.getCode();
    }

    public BusinessException(HttpCodeEnum httpCodeEnum, String message) {
        super(message);
        this.code = httpCodeEnum.getCode();
    }

    public int getCode() {
        return code;
    }
}
