package com.wb.connect.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.wb.connect.MyApp;

import java.net.InetAddress;

import io.netty.buffer.ByteBuf;

import static android.content.Context.MODE_APPEND;

/**
 * Created by sam on 2017/5/23.
 */

public class StringHelper {


    public static String format(final String fmt, Object... params) {
        String f;
        if (params != null) {
            f = String.format(fmt, params);
        } else {
            f = fmt;
        }

        return f;
    }

    public static byte[] readByteBuf(ByteBuf buf) {

        byte[] bytes;
        int offset;
        int length = buf.readableBytes();

        if (buf.hasArray()) {
            bytes = buf.array();
            offset = buf.arrayOffset();
        } else {
            bytes = new byte[length];
            buf.getBytes(buf.readerIndex(), bytes);
            offset = 0;
        }
        return bytes;
    }

    public static  String getWifiIpaddr(){

        //获取wifi服务
        WifiManager wifiManager = (WifiManager)  MyApp.applicationContext.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);

        return ip;
    }

    /**
     * convert ipaddress ;
     * @param ip eg  "192.168.1.120"
     * @return
     * @throws Exception
     */
    public static InetAddress convert2IpAddress(String ip) throws Exception{

        String[] ipStr = ip.split("\\.");
        byte[] ipBuf = new byte[4];
        for(int i = 0; i < 4; i++){
            ipBuf[i] = (byte)(Integer.parseInt(ipStr[i])&0xff);
            //ipBuf[i] =  (byte)Integer.parseInt(ipStr[i]);
            //ipBuf[i]= Byte.parseByte(ipStr[i]);
        }

        byte[] bytes= new byte[] {
                (byte)Integer.parseInt(ipStr[0]), (byte)Integer.parseInt(ipStr[1]), (byte)Integer.parseInt(ipStr[2]), (byte)Integer.parseInt(ipStr[3])
        };
/****
        byte[] bb= new byte[] {
                (byte)192, (byte)168, (byte)1, (byte)106};

        byte[] bytes22= new byte[] {
                (byte)Integer.parseInt("192"), (byte)Integer.parseInt("168"), (byte)Integer.parseInt("1"), (byte)Integer.parseInt("106")
        };
***/
        return InetAddress.getByAddress(bytes);
    }

    private  static String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

    public static void saveKey(String key,String value){

        SharedPreferences conn_keys = MyApp.applicationContext.getSharedPreferences("CONN_KEYS", MODE_APPEND);
        SharedPreferences.Editor edit = conn_keys.edit();
        //edit.clear();
        edit.putString(key, value.trim());
        edit.commit();
    }

    public static String getKey(String key){

        SharedPreferences conn_keys = MyApp.applicationContext.getSharedPreferences("CONN_KEYS", MODE_APPEND);

        return conn_keys.getString(key,"");
    }
}
