package com.example.Client.netty.handler;

import com.example.common.Message.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        //接收到response，给channel设计别名，让sendRequest里读取response
        //将服务端返回的RpcResponse绑定到当前的channel属性,以便后续逻辑能够通过channel获取该响应数据
        AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
        channelHandlerContext.channel().attr(key).set(rpcResponse);
        channelHandlerContext.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        cause.printStackTrace();
        channelHandlerContext.close();
    }
}
//SimpleChannelInboundHandler<RpcResponse>是Netty中用于处理服务器端响应的处理器
