/*
 * Copyright (C) 2017 zhouyou(478319399@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhouyou.http.exception;

import android.net.ParseException;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import com.zhouyou.http.model.ApiResult;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.NotSerializableException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

import static com.zhouyou.http.constant.ErrorEnum.CAST_ERROR;
import static com.zhouyou.http.constant.ErrorEnum.NETWORK_ERROR;
import static com.zhouyou.http.constant.ErrorEnum.NULL_POINTER_EXCEPTION;
import static com.zhouyou.http.constant.ErrorEnum.PARSE_ERROR;
import static com.zhouyou.http.constant.ErrorEnum.SSL_ERROR;
import static com.zhouyou.http.constant.ErrorEnum.TIMEOUT_ERROR;
import static com.zhouyou.http.constant.ErrorEnum.UNKNOWN;
import static com.zhouyou.http.constant.ErrorEnum.UNKNOWN_HOST_ERROR;


/**
 * <p>描述：统一处理了API异常错误</p>
 *
 * @author Administrator
 */
@SuppressWarnings("deprecation")
public class ApiException extends Exception {

    private final int code;
    private String message;


    public ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
        this.message = throwable.getMessage();
    }

    public ApiException(Throwable throwable, int code, String message) {
        super(throwable);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public static boolean isOk(ApiResult apiResult) {
        if (apiResult == null) {
            return false;
        }
        return apiResult.isOk();
    }

    public static ApiException handleException(Throwable e) {
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            return new ApiException(httpException, httpException.code(), httpException.getMessage());
        } else if (e instanceof ServerException) {
            ServerException resultException = (ServerException) e;
            return new ApiException(resultException, resultException.getErrCode(), resultException.getMessage());
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof JsonSerializer
                || e instanceof NotSerializableException
                || e instanceof ParseException) {
            return new ApiException(e, PARSE_ERROR.getCode(), PARSE_ERROR.getMsg());
        } else if (e instanceof ClassCastException) {
            return new ApiException(e, CAST_ERROR.getCode(), CAST_ERROR.getMsg());
        } else if (e instanceof ConnectException) {
            return new ApiException(e, NETWORK_ERROR.getCode(), NETWORK_ERROR.getMsg());
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            return new ApiException(e, SSL_ERROR.getCode(), SSL_ERROR.getMsg());
        } else if (e instanceof ConnectTimeoutException) {
            return new ApiException(e, TIMEOUT_ERROR.getCode(), TIMEOUT_ERROR.getMsg());
        } else if (e instanceof java.net.SocketTimeoutException) {
            return new ApiException(e, TIMEOUT_ERROR.getCode(), TIMEOUT_ERROR.getMsg());
        } else if (e instanceof UnknownHostException) {
            return new ApiException(e, UNKNOWN_HOST_ERROR.getCode(), UNKNOWN_HOST_ERROR.getMsg());
        } else if (e instanceof NullPointerException) {
            return new ApiException(e, NULL_POINTER_EXCEPTION.getCode(), NULL_POINTER_EXCEPTION.getMsg());
        } else {
            return new ApiException(e, UNKNOWN.getCode(), UNKNOWN.getMsg());
        }
    }

    @Override
    public String getMessage() {
        return message;
    }

}