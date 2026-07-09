package com.haoclass.coupon.interfaces.handler;

import com.haoclass.common.exception.BusinessException;
import com.haoclass.common.exception.ErrorCode;
import com.haoclass.common.result.Result;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException exception) {
        log.warn("Business exception: code={}, message={}", exception.getCode(), exception.getMessage());
        return Result.fail(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<Void> handleValidationException(Exception exception) {
        FieldError fieldError;
        if (exception instanceof MethodArgumentNotValidException validException) {
            fieldError = validException.getBindingResult().getFieldError();
        } else {
            fieldError = ((BindException) exception).getBindingResult().getFieldError();
        }
        String message = fieldError == null ? ErrorCode.BAD_REQUEST.getMessage() : fieldError.getDefaultMessage();
        return Result.fail(ErrorCode.BAD_REQUEST.getCode(), message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().stream()
                .findFirst()
                .map(violation -> violation.getMessage())
                .orElse(ErrorCode.BAD_REQUEST.getMessage());
        return Result.fail(ErrorCode.BAD_REQUEST.getCode(), message);
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class
    })
    public Result<Void> handleBadRequestException(Exception exception) {
        return Result.fail(
                ErrorCode.BAD_REQUEST.getCode(),
                Objects.toString(exception.getMessage(), ErrorCode.BAD_REQUEST.getMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception exception) {
        log.error("Unexpected system exception", exception);
        return Result.fail(ErrorCode.INTERNAL_ERROR.getCode(), ErrorCode.INTERNAL_ERROR.getMessage());
    }
}
