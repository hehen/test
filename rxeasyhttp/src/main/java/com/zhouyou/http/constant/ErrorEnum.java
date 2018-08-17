package com.zhouyou.http.constant;

/**
 * @author wenlu
 * @desc 约定异常
 * @date 2018/8/17 14:10
 */
public enum ErrorEnum {

    /**
     * 错误码
     */
    UNKNOWN(1000, "未知错误"),
    PARSE_ERROR(1001, "解析错误"),
    NETWORK_ERROR(1001, "网络错误"),
    HTTP_ERROR(1001, "协议出错"),
    SSL_ERROR(1001, "证书出错"),
    TIMEOUT_ERROR(1001, "连接超时"),
    INVOKE_ERROR(1001, "调用错误"),
    CAST_ERROR(1001, "类转换错误"),
    REQUEST_CANCEL(1001, "请求取消"),
    UNKNOWN_HOST_ERROR(1001, "未知主机错误"),
    NULL_POINTER_EXCEPTION(1001, "空指针错误");

    /**
     * 错误码
     */
    private int code;
    /**
     * 错误信息
     */
    private String msg;

    ErrorEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "ErrorEnum{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
