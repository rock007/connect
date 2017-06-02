package com.wb.connect.socket;

import android.util.Log;

import com.wb.connect.helper.StringHelper;
import com.wb.connect.socket.handler.NetMsgHeaderHandler;
import com.wb.connect.socket.header.NetMsgHeader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by sam on 2017/5/23.
 */

public class TcpWorkJob implements Runnable{

    private final String TAG=TcpWorkJob.class.getName();

    private int port=10023;
    private String host="192.168.1.105";

    private EventLoopGroup workerGroup;

    private Bootstrap bootstrap;

    private ChannelHandler channelHandler;

    private Channel channel;

    private List<Map<String,Object>> data;

    NetMsgHeaderHandler.OnNextProcessListener listener;


    public  TcpWorkJob(List<Map<String,Object>> d,NetMsgHeaderHandler.OnNextProcessListener listener1) {

        workerGroup = new NioEventLoopGroup();

        data=d;
        listener=listener1;
    }

    @Override
    public void run()  {

        try {
            Log.d(TAG,"connect server run ........ start");

            host= StringHelper.getKey("server_host");

            if(StringUtils.isEmpty(host)){

                Log.w(TAG,"没有找到服务器，不执行");

                return;
            }

            bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NetMsgHeaderHandler(data,listener));
                    }
                });

            ChannelFuture channelFuture = bootstrap.connect(host, port)
                                                    .sync();
            /***
            channel=channelFuture.channel();

            for(Map<String,Object> item:data){

                //Map<String,Object> item= data.get(sendIndex);
                String fileName=item.get("file_name").toString();
                File file=(File) item.get("file");

                byte[] fileBytes= FileUtils.readFileToByteArray(file);
                byte[] respBuf = NetMsgHeader.newMsg(NetMsgHeaderHandler.ai.getAndIncrement(), NetMsgHeader.CMD_ID_UPLOAD, fileBytes,fileName);

                channel.writeAndFlush(channel.alloc().buffer().writeBytes(respBuf));

            }
            ****/

            // Wait until the connection is closed.
            channelFuture.channel()
                    .closeFuture()
                    .sync();

            Log.d(TAG,"connect server started");
        }
        catch (Exception e) {
            Log.e(TAG,"ConnectServer:start,exception:", e);
            listener.onNotifyMsg(e.getMessage());

        }finally {
            workerGroup.shutdownGracefully();

        }
    }

}
