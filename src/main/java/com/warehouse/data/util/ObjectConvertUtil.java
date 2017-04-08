package com.warehouse.data.util;

import com.alibaba.fastjson.JSON;
import com.warehouse.data.netty.model.Event;
import com.warehouse.data.netty.model.RecvieMessage;
import com.warehouse.data.netty.model.RequestFile;
import com.warehouse.data.netty.model.ResponseFile;
import com.warehouse.data.netty.model.Secure;

public class ObjectConvertUtil {

    public static String convertModle(Secure secure) {
        RecvieMessage recevie = new RecvieMessage();
        recevie.setData(JSON.toJSONString(secure));
        recevie.setMsgType(Event.MESSAGE_TYPE_SECURE_MODEL);
        return JSON.toJSONString(recevie);
    }

    public static String convertModle(ResponseFile response) {
        RecvieMessage recevie = new RecvieMessage();
        recevie.setData(JSON.toJSONString(response));
        recevie.setMsgType(Event.MESSAGE_TYPE_RESPONSE_FILE);
        return JSON.toJSONString(recevie);
    }

    public static String convertModle(RequestFile requst) {
        RecvieMessage recevie = new RecvieMessage();
        recevie.setData(JSON.toJSONString(requst));
        recevie.setMsgType(Event.MESSAGE_TYPE_REQUEST_FILE);
        return JSON.toJSONString(recevie);
    }

    public static Object convertModle(String recviejson) {
        RecvieMessage recvie = (RecvieMessage) JSON.parseObject(recviejson, RecvieMessage.class);
        Object obj = null;
        switch (recvie.getMsgType()) {
            case Event.MESSAGE_TYPE_SECURE_MODEL:
                obj = (Secure) JSON.parseObject(recvie.getData().toString(), Secure.class);
                break;
            case Event.MESSAGE_TYPE_REQUEST_FILE:
                obj = (RequestFile) JSON.parseObject(recvie.getData().toString(), RequestFile.class);
                break;
            case Event.MESSAGE_TYPE_RESPONSE_FILE:
                obj = (ResponseFile) JSON.parseObject(recvie.getData().toString(), ResponseFile.class);
                break;
        }
        return obj;
    }

    public static String request(Object obj) {
        if (obj instanceof Secure) {
            Secure secure = (Secure) obj;
            return convertModle(secure);
        } else if (obj instanceof RequestFile) {
            RequestFile requestFile = (RequestFile) obj;
            return convertModle(requestFile);
        } else if (obj instanceof ResponseFile) {
            ResponseFile responseFile = (ResponseFile) obj;
            return convertModle(responseFile);
        } else {
            return null;
        }

    }


}
