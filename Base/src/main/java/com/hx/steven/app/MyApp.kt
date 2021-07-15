package com.hx.steven.app

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
        // 初始化koin
        startKoin {
            androidLogger()
            androidContext(this@MyApp)
            modules(allModule)
        }
        super.onCreate()
    }
}