package com.hx.steven.manager

import android.app.Application
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheEntity
import com.lzy.okgo.cache.CacheMode
import com.lzy.okgo.cookie.CookieJarImpl
import com.lzy.okgo.cookie.store.DBCookieStore
import com.lzy.okgo.https.HttpsUtils
import com.lzy.okgo.https.HttpsUtils.SSLParams
import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import com.tencent.mmkv.MMKV
import okhttp3.OkHttpClient
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import javax.net.ssl.X509TrustManager


/**
 * okGo管理
 */
class OkGoManager {
    fun init(context: Application) {
        val builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor("OkGo")
        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY)
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO)
        builder.addInterceptor(loggingInterceptor)
        builder.addInterceptor { chain ->
            val token = MMKV.defaultMMKV().decodeString("token", "")
            val request = chain.request().newBuilder().addHeader("Authorization", token).build()
            chain.proceed(request)
        }

        //全局的连接超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的读取超时时间
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);

        //使用数据库保持cookie，如果cookie不过期，则一直有效
        builder.cookieJar(CookieJarImpl(DBCookieStore(context)))

        val sslParams2: SSLParams = HttpsUtils.getSslSocketFactory(SafeTrustManager())
        builder.sslSocketFactory(sslParams2.sSLSocketFactory, sslParams2.trustManager)


        OkGo.getInstance().init(context)                       //必须调用初始化
            .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置将使用默认的
            .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
            .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
            .setRetryCount(0)                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
    }

    private class SafeTrustManager : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String?) {
            try {
                for (certificate in chain) {
                    certificate.checkValidity() //检查证书是否过期，签名是否通过等
                }
            } catch (e: Exception) {
                throw CertificateException(e)
            }
        }

        override fun getAcceptedIssuers(): Array<X509Certificate?> {
            return arrayOfNulls<X509Certificate>(0)
        }
    }
}