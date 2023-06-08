package com.potato.template.utils;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    /**
     * http返回码
     */
    private Integer code;
    /**
     * http返回码对应的信息
     */
    private String message;
    /**
     * 返回的数据，用泛型定义
     */
    private T data;

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(HttpCodeEnum httpCodeEnum, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(HttpCodeEnum httpCodeEnum) {
        this(httpCodeEnum.getCode(), httpCodeEnum.getMessage());
    }

    public static Result ok(){
        Result resultUtil = new Result(HttpCodeEnum.OK);
        return resultUtil;
    }
    public static Result ok(String message){
        Result resultUtil = new Result(HttpCodeEnum.OK,message);
        return resultUtil;
    }
    public static Result error(Integer code,String message){
        Result resultUtil = new Result(code,message);
        return resultUtil;
    }

    public static Result error(HttpCodeEnum httpCodeEnum,String message){
        Result resultUtil = new Result(httpCodeEnum.getCode(),message);
        return resultUtil;
    }


    public Result message(String message) {
        this.setMessage(message);
        return this;
    }

    public Result data(T data) {
        this.setData(data);
        return this;
    }
}
