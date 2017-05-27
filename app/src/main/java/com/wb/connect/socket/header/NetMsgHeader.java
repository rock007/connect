package com.wb.connect.socket.header;

import android.util.Log;

import com.wb.connect.helper.StringHelper;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sam on 2017/5/23.
 */

public class NetMsgHeader {

    private final String TAG=NetMsgHeader.class.getName();

    private static final int FIXED_HEADER_SKIP = 4  + 4 + 36 + 4;

    public static final int CMDID_NOOPING = 6;//心跳
    public static final int CMDID_NOOPING_RESP = 6;

    public static final int CMD_ID_LOGIN = 1; //登录
    public static final int CMD_ID_CONTROL = 2;//控制
    public static final int CMD_ID_UPLOAD = 3;//控制

    public int headLength;
    public int cmdId;
    public int seq;
    public byte[] option=new byte[36];

    public byte[] body;

    public static class InvalidHeaderException extends Exception {

        /**
         *
         */
        private static final long serialVersionUID = 8274020991050138681L;

        public InvalidHeaderException(String message) {
            super(message);
        }
    }

    /**
     * Decode NetMsgHeader from InputStream
     *
     * @param is close input stream yourself
     * @return
     * @throws IOException
     */
    public boolean decode(final InputStream is) throws InvalidHeaderException {
        final DataInputStream dis = new DataInputStream(is);

        try {
            headLength = dis.readInt();

            cmdId = dis.readInt();
            seq = dis.readInt();

            option=new byte[36];
            dis.readFully(option);

            int bodyLen = dis.readInt();

            Log.d(TAG,StringHelper.format("dump cmdid=%d, seq=%d, packlen=%d",  cmdId, seq, (headLength + bodyLen)));

            // read body?
            if (bodyLen > 0) {
                body = new byte[bodyLen];
                dis.readFully(body);

            } else {
                // no body?!
                switch (cmdId) {
                    case CMDID_NOOPING:
                        break;

                    default:
                        throw new InvalidHeaderException("invalid header body, cmdid:" + cmdId);
                }
            }

            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public byte[] encode() throws InvalidHeaderException {
        if (body == null && cmdId != CMDID_NOOPING && cmdId != CMDID_NOOPING_RESP) {
            throw new InvalidHeaderException("invalid header body");
        }

        final int headerLength = FIXED_HEADER_SKIP ;
        final int bodyLength = (body == null ? 0 : body.length);
        final int packLength = headerLength + bodyLength;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(packLength);

        try {
            final DataOutputStream dos = new DataOutputStream(baos);

            dos.writeInt(headerLength);
            dos.writeInt(cmdId);
            dos.writeInt(seq);

            if(option.length!=36){
                throw new InvalidHeaderException("ticket is wrong length");
            }
            dos.write(option);
            dos.writeInt(bodyLength);

            if (body != null) {
                dos.write(body);
            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                baos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return baos.toByteArray();
    }

    public static byte[] newMsg(int seq,int cmdId,String body){

        return newMsg(seq,cmdId,body,null);
    }

    public static byte[] newMsg(int seq,int cmdId,String body,String option){

        try {
            NetMsgHeader msgHeader = new NetMsgHeader();
            msgHeader.seq = seq;
            msgHeader.cmdId = cmdId;
            msgHeader.option=option!=null?option.getBytes():new byte[36];
            msgHeader.body =body!=null?body.getBytes():" ".getBytes();

            return msgHeader.encode();
        } catch (InvalidHeaderException e) {

            e.printStackTrace();
        }

        return "err".getBytes();
    }

    public static byte[] newMsg(int seq,int cmdId,byte[] body,String option){

        try {

            if(option!=null){

                option=option+"                                    ".substring(0,36-option.length());
            }


            NetMsgHeader msgHeader = new NetMsgHeader();
            msgHeader.seq = seq;
            msgHeader.cmdId = cmdId;
            msgHeader.option=option!=null?option.getBytes():new byte[36];
            msgHeader.body =body!=null?body:" ".getBytes();

            return msgHeader.encode();
        } catch (InvalidHeaderException e) {

            e.printStackTrace();
        }

        return "err".getBytes();
    }
}
