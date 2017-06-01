package com.wb.connect.socket;

/**
 * Created by sam on 2017/6/1.
 */

 import android.util.Log;

 import com.wb.connect.socket.handler.UdpClientHandler;

 import io.netty.bootstrap.Bootstrap;
 import io.netty.buffer.ByteBuf;
 import io.netty.channel.Channel;
 import io.netty.channel.ChannelHandlerContext;
 import io.netty.channel.ChannelInitializer;
 import io.netty.channel.ChannelOption;
 import io.netty.channel.ChannelPromise;
 import io.netty.channel.EventLoopGroup;
 import io.netty.channel.nio.NioEventLoopGroup;
 import io.netty.channel.socket.DatagramChannel;
 import io.netty.channel.socket.DatagramPacket;
 import io.netty.channel.socket.SocketChannel;
 import io.netty.channel.socket.nio.NioDatagramChannel;
 import io.netty.channel.socket.nio.NioSocketChannel;
 import io.netty.handler.codec.DelimiterBasedFrameDecoder;
 import io.netty.handler.codec.Delimiters;
 import io.netty.handler.codec.MessageToMessageEncoder;
 import io.netty.handler.codec.string.StringDecoder;
 import io.netty.handler.codec.string.StringEncoder;
 import io.netty.util.CharsetUtil;
 import io.netty.util.ReferenceCountUtil;

 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.net.InetAddress;
 import java.net.InetSocketAddress;
 import java.util.List;

public class UdpClient {

    private final String TAG=UdpClient.class.getName();

    public  InetAddress host ;
    public  int port = 10024;

    public UdpClient(final InetAddress host, final int port) {
        this.host = host;
        this.port = port;
    }

    /****
     *
     * @param msg
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public void send(String msg) throws InterruptedException, IOException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    //.handler(new UdpClientHandler());

                    .handler(new MessageToMessageEncoder<String>() {
                        @Override
                        protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
                            ByteBuf buf = ctx.alloc().buffer();
                            buf.writeBytes(msg.getBytes(CharsetUtil.UTF_8));
                            //buf.writeByte(LogEvent.SEPARATOR);
                            //buf.writeBytes(msg.getMsg().getBytes(CharsetUtil.UTF_8));
                            out.add(new DatagramPacket(buf,new  InetSocketAddress (host,port) ));
                        }
                    }) ;

            // 连接服务端
            Channel ch = b.connect(host, port).sync().channel();

            ch.writeAndFlush(msg + "\r\n");


        } finally {
            // The connection is closed automatically on shutdown.
            group.shutdownGracefully();
        }
    }

}