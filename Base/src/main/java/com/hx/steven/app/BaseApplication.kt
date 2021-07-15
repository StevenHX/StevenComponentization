package com.hx.steven.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Process
import android.text.TextUtils
import android.util.Log
import androidx.multidex.MultiDex
import cn.jpush.android.api.JPushInterface
import com.alibaba.android.arouter.launcher.ARouter
import com.hx.steven.BuildConfig
import com.hx.steven.manager.OkGoManager
import com.hx.steven.manager.WxManager
import com.hx.steven.util.ActivityManagerUtil
import com.hx.steven.util.AppUtils
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.tencent.bugly.Bugly
import com.tencent.bugly.crashreport.CrashReport.UserStrategy
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import org.koin.android.ext.android.inject
import java.util.*

/**
 * Created by user on 2018/1/15.
 */
abstract class BaseApplication : Application() {
    abstract val appInitBuilder: AppInitBuilder
    private val okGoManager by inject<OkGoManager>()
    override fun onCreate() {
        super.onCreate()
        appContext = this
        // 初始化阿里路由
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)
        if (appInitBuilder.isInitLogger) initLogger()
        if (appInitBuilder.isInitX5) initX5WebView()
        if (appInitBuilder.isInitWXSDK) WxManager.getInstance().regToWx()
        if (appInitBuilder.isInitBugly) initBugly()
        if (appInitBuilder.isInitJpush) initJpush()
        if (appInitBuilder.isInitOkGo) okGoManager.init(this)
        registerActivityLifecycleCallbacks(SwitchBackgroundCallbacks())
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        ARouter.getInstance().destroy()
    }

    private fun initJpush() {
        JPushInterface.setDebugMode(TextUtils.equals(BuildConfig.BUILD_TYPE, "debug"))
        JPushInterface.init(this)
    }

    private fun initBugly() {
        // 获取当前包名
        val packageName = packageName
        // 获取当前进程名
        val processName = AppUtils.getProcessName(Process.myPid())
        // 设置是否为上报进程
        val strategy = UserStrategy(this)
        strategy.isUploadProcess = processName == null || processName == packageName
        Bugly.init(
            this,
            BuildConfig.buglyAppId,
            TextUtils.equals(BuildConfig.BUILD_TYPE, "debug"),
            strategy
        )
    }

    private fun initLogger() {
        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(true) // (Optional) Whether to show thread info or not. Default true
            .methodCount(2) // (Optional) How many method line to show. Default 2
            .methodOffset(7) // (Optional) Hides internal method calls up to offset. Default 5
            .tag("StevenBase") // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
    }

    fun initX5WebView() {
        val cb: PreInitCallback = object : PreInitCallback {
            override fun onViewInitFinished(arg0: Boolean) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d(TAG, " onViewInitFinished is $arg0")
            }

            override fun onCoreInitFinished() {}
        }
        // 防止和crosswalk冲突、多进程加快初始化速度
        val map: HashMap<String?, Any?> = HashMap<String?, Any?>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_PRIVATE_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)
        //x5内核初始化接口
        QbSdk.initX5Environment(appContext, cb)
    }

    private class SwitchBackgroundCallbacks : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
            ActivityManagerUtil.getInstance().pushOneActivity(activity)
        }

        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {
            ActivityManagerUtil.getInstance().popOneActivity(activity)
        }
    }

    companion object {
        private const val TAG = "BaseApplication"

        /**
         * 获取application context
         *
         * @return
         */
        var appContext: BaseApplication? = null
            private set
    }
}