package com.taotao.pojo;

import java.io.Serializable;

public class PictureResult implements Serializable{
    private int code;
    private String msg;
    private PicturData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public PicturData getData() {
        return data;
    }

    public void setData(PicturData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PictureResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
