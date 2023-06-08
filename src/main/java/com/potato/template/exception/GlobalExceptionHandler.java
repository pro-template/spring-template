package com.potato.template.exception;

import com.potato.template.utils.HttpCodeEnum;
import com.potato.template.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * 全局异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 自定义异常
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> businessExceptionHandler(BusinessException e) {
        log.error("businessException: " + e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验不通过异常
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result argumentNotValidErrorHandler(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        // 构建错误响应对象
        String message = fieldErrors.get(fieldErrors.size()-1).getDefaultMessage();
        log.error("参数错误:"+message);
        return Result.error(HttpCodeEnum.PARAMS_ERROR,message);
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException", e);
        return Result.error(HttpCodeEnum.SYSTEM_ERROR, e.getMessage());
    }
}
