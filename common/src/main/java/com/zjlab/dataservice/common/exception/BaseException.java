package com.zjlab.dataservice.common.exception;

import com.zjlab.dataservice.common.constant.enums.ResultCode;
import lombok.Getter;
import lombok.Setter;

/**
 * BaseException
 */
@Getter
@Setter
public class BaseException extends RuntimeException{

    private Integer code;
    private String message;


    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BaseException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BaseException(Integer code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public BaseException(ResultCode resultCode){
        this.code = resultCode.getCode();
        this.message=resultCode.getMessage();
    }
}