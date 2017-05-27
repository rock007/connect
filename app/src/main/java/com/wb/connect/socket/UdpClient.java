package com.wb.connect.socket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by sam on 2017/5/23.
 */

public class UdpClient {

    public static String sendUdp(InetAddress ipaddress, Integer point, String msg){

        String recvStr="" ;

        DatagramSocket client;

        byte[] sendBuf;

        byte[] recvBuf = new byte[100];

        try{

            sendBuf = msg.getBytes();

            client=new DatagramSocket();
            client.setSoTimeout(5000);

            DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, ipaddress, point);
            client.send(sendPacket);

            DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
            client.receive(recvPacket);

            recvStr = new String(recvPacket.getData(), 0, recvPacket.getLength());

            client.close();

        }catch(Exception ex){

        }
        return    recvStr;
    }
}
