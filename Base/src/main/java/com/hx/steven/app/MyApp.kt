package com.hx.steven.app

import com.alibaba.android.arouter.launcher.ARouter
import com.hx.steven.BuildConfig
import com.hx.steven.di.allModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Created by user on 2018/1/15.
 */
class MyApp : BaseApplication() {
    override fun getAppInitBuilder(): AppInitBuilder {
        return AppInitBuilder.Builder()
            .setInitLogger(true)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
//        try {
//            RetrofitNetManager.getInstance().init("https://www.wanandroid.com");
//            RetrofitNetManager.getInstance().setApiService(RetrofitNetManager.getInstance().getRetrofit().create(ApiService.class));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)
        startKoin {
            androidLogger()
            androidContext(this@MyApp)
            modules(allModule)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        ARouter.getInstance().destroy()
    }
}