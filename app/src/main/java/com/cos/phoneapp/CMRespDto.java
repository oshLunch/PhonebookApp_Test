package com.cos.phoneapp;

public class CMRespDto<T> {
    private int code;
    private T data;

    public CMRespDto(){

    }

    public CMRespDto(int code) {
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
