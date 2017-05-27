package com.wb.connect.socket.handler;

import android.os.Build;
import android.util.Log;

import com.wb.connect.helper.StringHelper;
import com.wb.connect.socket.header.NetMsgHeader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by sam on 2017/5/23.
 */

public class NetMsgHeaderHandler extends ChannelInboundHandlerAdapter {

    private final String TAG=NetMsgHeaderHandler.class.getName();

    private static final int CMD_ID_HELLO_VALUE = 0;

    private List<Map<String,Object>> data;
    private int sendIndex=0;
    public static AtomicInteger ai=new AtomicInteger(0);

    public NetMsgHeaderHandler(List<Map<String,Object>> d) {
        super();

        //init
        data=d;
        sendIndex=0;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        Log.d(TAG,"client active! " + ctx.toString());

        /****
        final ChannelFuture f = ctx.writeAndFlush(ctx.alloc().buffer().writeBytes(NetMsgHeader.newMsg(ai.getAndIncrement(), NetMsgHeader.CMD_ID_LOGIN, "who")));
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                assert f == future;
                // ctx.close();
            }
        });
        ***/
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        byte[] respBuf;

        try {
            String option="";

            // decode request
            ByteBuf byteBuf= (ByteBuf) msg;

            byte[] receiveBytes = StringHelper.readByteBuf(byteBuf);

            NetMsgHeader msgXp = new NetMsgHeader();

            boolean ret = msgXp.decode(new ByteArrayInputStream(receiveBytes));

            if(!ret) {

                Log.w(TAG,"wrong msg ,close");
                ctx.close();
                return;
            }

            //获得客户端
            if(msgXp.option!=null){

                option=new String( msgXp.option).trim();
            }

            if(option!=null&&!"".equals(option)){

                //SessionManager.updateActiveTime(ticket,System.currentTimeMillis());

            }

            Log.d(TAG,StringHelper.format("client req, cmdId=%d, seq=%d,option=%s", msgXp.cmdId, msgXp.seq,option));

            /***
            if(msgXp.cmdId!=NetMsgHeader.CMD_ID_LOGIN&& StringUtils.isEmpty(option) ){

                //参数不对，
                Log.d(TAG,"参数不对，断开连接");
                ctx.close();
                return;
            }
            ***/

            switch (msgXp.cmdId) {
                case NetMsgHeader.CMD_ID_UPLOAD:
                    // upload file

                    //收到回复
                    if(sendIndex<data.size()){

                        Map<String,Object> item= data.get(sendIndex);
                        String fileName=item.get("file_name").toString();
                        File file=(File) item.get("file");

                        byte[] fileBytes=FileUtils.readFileToByteArray(file);
                        respBuf =NetMsgHeader.newMsg(ai.getAndIncrement(), NetMsgHeader.CMD_ID_UPLOAD, fileBytes,fileName); //msgXp.encode();

                        ctx.writeAndFlush(ctx.alloc().buffer().writeBytes(respBuf));

                        sendIndex++;

                    }else{
                        respBuf =NetMsgHeader.newMsg(ai.getAndIncrement(), NetMsgHeader.CMD_ID_UPLOAD, "finish","");
                        ctx.writeAndFlush(ctx.alloc().buffer().writeBytes(respBuf));
                        ctx.close();
                    }

                    break;
                case NetMsgHeader.CMD_ID_LOGIN:
                    //登录
                    String bodyStr=new String( msgXp.body);

                    String args[]= bodyStr.split(",");

                    if(args.length!=2){

                        Log.d(TAG,"参数不正确");
                        ctx.close();
                        return;
                    }

                    String deviceModel=Build.MODEL.toString();

                    respBuf =NetMsgHeader.newMsg(ai.getAndIncrement(), NetMsgHeader.CMD_ID_LOGIN, "ok",deviceModel); //msgXp.encode();

                    ctx.writeAndFlush(ctx.alloc().buffer().writeBytes(respBuf));

                    break;

                case NetMsgHeader.CMDID_NOOPING:

                    //心跳
                    byte[] respBuf2 =NetMsgHeader.newMsg(ai.getAndIncrement(), NetMsgHeader.CMDID_NOOPING, "ok",option);// msgXp.encode();

                    ctx.writeAndFlush(ctx.alloc().buffer().writeBytes(respBuf2));
                    break;
                default:
                    break;
            }
        }catch (Exception e) {
            e.printStackTrace();

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();

    }

    /***
     * 心跳由客户端发送
     * @author sam
     *
     */
    public class HeartbeatTask extends TimerTask {

        @Override
        public void run() {
            Log.d(TAG,StringHelper.format("send Heartbeat check client alive  per 5 minutes, " + this));

        }
    }
}
