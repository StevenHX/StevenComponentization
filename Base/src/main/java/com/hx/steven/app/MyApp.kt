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

    override val appInitBuilder: AppInitBuilder
        get() = AppInitBuilder.Builder()
            .setInitLogger(true)
            .setOkGo(true)
            .build()

    override fun onCreate() {
        // 初始化ARouter
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)
        // 初始化koin
        startKoin {
            androidLogger()
            androidContext(this@MyApp)
            modules(allModule)
        }
        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
        ARouter.getInstance().destroy()
    }
}