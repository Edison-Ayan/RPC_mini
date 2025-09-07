package com.example.Client.rpcClient;

import com.example.common.Message.RpcRequest;
import com.example.common.Message.RpcResponse;

public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
}
