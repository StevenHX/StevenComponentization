package com.hx.steven.mina;

import android.util.Log;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

/**
 * 被动型心跳机制，服务器在接受到客户端连接以后被动接受心跳请求，当在规定时间内没有收到客户端心跳请求时将客户端连接关闭
 * @ClassName KeepAliveMessageFactoryImpl
 * @Description 内部类，实现KeepAliveMessageFactory（心跳工厂）
 * @author cruise
 *
 */
class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory {
    private static final String TAG = "KeepAliveMessageFactory";
    /* 服务器不会给客户端发送请求包，因此不关注请求包，直接返回 false   */
    @Override
    public boolean isRequest(IoSession session, Object message) {
        return false;
    }

    /* 客户端关注请求反馈，因此判断 mesaage 是否是反馈包 */
    @Override
    public boolean isResponse(IoSession session, Object message) {
        Log.d(TAG,"响应预设信息: " + message);
        return message.equals("HEART_BEAT_RESPONSE");
    }

    /* 获取心跳请求包 non-null */
    @Override
    public Object getRequest(IoSession session) {
        Log.d(TAG,"请求预设信息: HEART_BEAT_REQUEST");
        return "HEART_BEAT_REQUEST";
    }

    /* 服务器不会给客户端发送心跳请求，客户端当然也不用反馈，该方法返回 null  */
    @Override
    public Object getResponse(IoSession session, Object request) {
        return null;
    }
}