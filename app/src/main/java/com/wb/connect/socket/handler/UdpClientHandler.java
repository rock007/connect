package com.wb.connect.socket.handler;

import android.util.Log;

import com.wb.connect.socket.UdpClient;
import com.wb.connect.ui.SelectDirActivity;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by sam on 2017/6/1.
 */
import java.net.InetAddress;


public class UdpClientHandler extends SimpleChannelInboundHandler<String> {


    private final String TAG=UdpClientHandler.class.getName();

    public UdpClientHandler(){

    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // 收到消息直接打印输出
        Log.d(TAG,ctx.channel().remoteAddress() + " Say : " + msg);

        try {

            // 返回客户端消息 - 我已经接收到了你的消息
            ctx.writeAndFlush("Received your message !\n");

        }catch (Exception e) {
            e.printStackTrace();

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /*
     *
     * 覆盖 channelActive 方法 在channel被启用的时候触发 (在建立连接的时候)
     *
     * channelActive 和 channelInActive 在后面的内容中讲述，这里先不做详细的描述
     * */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        Log.d(TAG,"RamoteAddress : " + ctx.channel().remoteAddress() + " active !");
        Log.d(TAG,"Welcome to " + InetAddress.getLocalHost().getHostName() + " service!\n");
        //ctx.writeAndFlush( "Welcome to " + InetAddress.getLocalHost().getHostName() + " service!\n");

        super.channelActive(ctx);
    }
}

