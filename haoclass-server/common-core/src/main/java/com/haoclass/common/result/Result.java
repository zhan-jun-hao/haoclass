package com.haoclass.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 封装result类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> implements Serializable {

    private int code;

    private String msg;

    private T data;

    public static <T> Result<T> success(T data){
        return new Result<T>(200, "success", data);
    }

    public static <T> Result<T> success() {
        return new Result<T>(200, "success", null);
    }

    public static <T> Result<T> fail(String msg) {
        return new Result<T>(500, msg, null);
    }

    public static <T> Result<T> fail(int code, String msg) {
        return new Result<T>(code, msg, null);
    }
}

