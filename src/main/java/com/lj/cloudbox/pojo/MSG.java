package com.lj.cloudbox.pojo;

public class MSG {
    private static final  Integer SUCCESS_CODE = 200;
    private static final  Integer FAIL_CODE = 500;
    private Integer status;
    private String msg;
    private Object data;

    public MSG() {
    }

    public MSG(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public MSG(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public static MSG success(String msg) {
        return new MSG(SUCCESS_CODE, msg);
    }

    public static MSG fail(String msg) {
        return new MSG(FAIL_CODE, msg);
    }


    public static MSG success(String msg, Object data) {
        return new MSG(SUCCESS_CODE, msg, data);
    }

    public static MSG fail(String msg, Object data) {
        return new MSG(FAIL_CODE, msg, data);
    }

    @Override
    public String toString() {
        return "MSG{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public Integer getStatus() {
        return status;
    }

    public MSG setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public MSG setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Object getData() {
        return data;
    }

    public MSG setData(Object data) {
        this.data = data;
        return this;
    }


}
