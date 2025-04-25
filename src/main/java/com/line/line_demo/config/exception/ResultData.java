package com.line.line_demo.config.exception;

import org.springframework.http.HttpStatus;

/**.
 * @param <T>
 * @author Administrator
 */
public class ResultData<T> {

    public int code;

    private String msg;

    private T data;

    public int getCode() {
        return code;
    }


    public ResultData<T> setCode(HttpStatus resultCode) {
        this.code = resultCode.value();
        return this;
    }

    public ResultData<T> setCode(int resultCode) {
        this.code =resultCode;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public ResultData<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public ResultData<T> setData(T data) {
        this.data = data;
        return this;
    }

    public static <T> ResultData<T> resultData(int statusCode, String message) {
        return new ResultData<T>().setCode(statusCode).setMsg(message);
    }

    public static <T> ResultData<T> resultData(int statusCode, String message, T t) {
        ResultData<T> result = new ResultData<T>().setCode(statusCode).setMsg(message);
        result.setData(t);
        return result;
    }

}
