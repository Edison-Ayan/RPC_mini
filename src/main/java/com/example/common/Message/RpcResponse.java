package com.example.common.Message;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class RpcResponse implements Serializable {
    private int code;
    private String message;
    private Object data;
    private Class<?> dataType;

    public static RpcResponse success(Object data){
        return RpcResponse.builder().code(200).data(data).build();
    }

    public static RpcResponse fail(){
        return RpcResponse.builder().code(500).message("服务器错误").build();
    }


    public void setDataType(Class<?> dataType) {
        this.dataType = dataType;
    }
}
