package com.hx.steven.manager

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import androidx.collection.SimpleArrayMap
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class WebServiceManager {
    // 访问的服务器是否由dotNet开发
    private var isDotNet = false

    // 线程池的大小
    private var threadSize = 5

    // 创建一个可重用固定线程数的线程池，以共享的无界队列方式来运行这些线程
    private var threadPool: ExecutorService = Executors.newFixedThreadPool(threadSize)

    // 连接响应标示
    val SUCCESS_FLAG = 0
    val ERROR_FLAG = 1

    private lateinit var responseCallBack: Response

    // 4.用于子线程与主线程通信的Handler，网络请求成功时会在子线程发送一个消息，然后在主线程上接收
    private val responseHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            // 根据消息的arg1值判断调用哪个接口
            if (msg.arg1 == SUCCESS_FLAG) {
                responseCallBack.onSuccess(msg.obj as SoapObject)
            } else {
                responseCallBack.onError(
                    msg.obj as java.lang.Exception
                )
            }
        }
    }

    /**
     * 调用WebService接口，此方法只访问过用Java写的服务器
     *
     * @param url             WebService服务器地址
     * @param nameSpace       命名空间
     * @param methodName      WebService的调用方法名
     * @param mapParams       WebService的参数集合，可以为null
     * @param reponseCallBack 服务器响应接口
     */
    fun call(
        url: String?,
        nameSpace: String,
        methodName: String,
        mapParams: SimpleArrayMap<String?, String?>?,
        responseCallBack: Response
    ) {
        this.responseCallBack = responseCallBack
        // 1.创建HttpTransportSE对象，传递WebService服务器地址
        val transport = HttpTransportSE(url)
        transport.debug = true
        // 2.创建SoapObject对象用于传递请求参数
        val request = SoapObject(nameSpace, methodName)
        // 2.1.添加参数也可以不传
        if (mapParams != null) {
            for (index in 0 until mapParams.size()) {
                val key = mapParams.keyAt(index)
                val value = mapParams[key]
                request.addProperty(key, value)
            }
        }

        // 3.实例化SoapSerializationEnvelope，传入WebService的SOAP协议的版本号
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = isDotNet // 设置是否调用的是.Net开发的WebService
        envelope.setOutputSoapObject(request)


        // 5.提交一个子线程到线程池并在此线种内调用WebService
        if (threadPool.isShutdown) threadPool =
            Executors.newFixedThreadPool(threadSize)
        threadPool.submit {
            var result: SoapObject? = null
            try {
                // 解决EOFException
                System.setProperty("http.keepAlive", "false")
                // 连接服务器
                transport.call(null, envelope)
                if (envelope.response != null) {
                    // 获取服务器响应返回的SoapObject
                    result = envelope.bodyIn as SoapObject
                    // 将获取的消息利用Handler发送到主线程
                    responseHandler.sendMessage(
                        responseHandler.obtainMessage(
                            0,
                            SUCCESS_FLAG,
                            0,
                            result
                        )
                    )
                }
            } catch (e: IOException) {
                // 当call方法的第一个参数为null时会有一定的概念抛IO异常
                // 因此需要需要捕捉此异常后用命名空间加方法名作为参数重新连接
                e.printStackTrace()
                try {
                    transport.call(nameSpace + methodName, envelope)
                    if (envelope.response != null) {
                        // 获取服务器响应返回的SoapObject
                        result = envelope.bodyIn as SoapObject
                        // 将获取的消息利用Handler发送到主线程
                        responseHandler.sendMessage(
                            responseHandler.obtainMessage(
                                0,
                                SUCCESS_FLAG,
                                0,
                                result
                            )
                        )
                    }
                } catch (e1: Exception) {
                    responseHandler.sendMessage(responseHandler.obtainMessage(0, ERROR_FLAG, 0, e1))
                }
            } catch (e: XmlPullParserException) {
                responseHandler.sendMessage(responseHandler.obtainMessage(0, ERROR_FLAG, 0, e))
            }
        }
    }

    /**
     * 设置线程池的大小
     *
     * @param threadSize
     */
    fun setThreadSize(threadSize: Int) {
        this.threadSize = threadSize
        threadPool.shutdownNow()
        threadPool = Executors.newFixedThreadPool(this.threadSize)
    }

    fun setIsDotNet(isDotNet: Boolean?) {
        this.isDotNet = isDotNet!!
    }

    /**
     * 服务器响应接口，在响应后需要回调此接口
     */
    interface Response {
        fun onSuccess(result: SoapObject?)
        fun onError(e: Exception?)
    }
}