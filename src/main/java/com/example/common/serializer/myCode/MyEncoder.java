package com.example.common.serializer.myCode;

import com.example.common.Message.MessageType;
import com.example.common.Message.RpcRequest;
import com.example.common.Message.RpcResponse;
import com.example.common.serializer.mySerializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyEncoder extends MessageToByteEncoder {
    //MessageToByteEncoder时netty专门设计用来实现编码器的抽象类
    private Serializer serializer;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        System.out.println(o.getClass());
        if (o instanceof RpcRequest) {
            byteBuf.writeShort(MessageType.REQUEST.getCode());
        } else if (o instanceof RpcResponse) {
            byteBuf.writeShort(MessageType.RESPONSE.getCode());
        }
        byteBuf.writeShort(serializer.getType());
        byte[] serializeBytes = serializer.serialize(o);
        byteBuf.writeInt(serializeBytes.length);
        byteBuf.writeBytes(serializeBytes);
    }
}
