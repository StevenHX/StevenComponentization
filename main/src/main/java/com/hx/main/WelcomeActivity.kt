package com.hx.main

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hx.steven.activity.BaseActivity
import com.hx.steven.app.BaseApplication
import com.hx.steven.component.SuperTextView
import com.hx.steven.constant.Constants
import com.hx.steven.util.AppUtils
import com.hx.steven.util.JsonHelp
import com.orhanobut.logger.Logger
import com.steven.updatetool.CheckAppVersionListener
import com.steven.updatetool.UpdateModel
import com.steven.updatetool.UpdateUtil

@Route(path = Constants.A_WELCOME)
class WelcomeActivity : BaseActivity() {
    private lateinit var tvName: SuperTextView
    val updateUtil = UpdateUtil()
    override fun initView() {
        tvName = findViewById(R.id.tvName)
        val anim =
            AnimationUtils.loadAnimation(baseContext, R.anim.splash)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                tvName.start()
            }

            override fun onAnimationEnd(animation: Animation) {
                testUpdate()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        tvName.startAnimation(anim)
    }

    override fun getContentId(): Int {
        return R.layout.welcome_activity
    }

    private fun openMain() {
        ARouter.getInstance().build(Constants.A_MAIN).navigation()
        finish()
    }

    fun testUpdate() {
        Logger.d(AppUtils.getMetaDataInt(BaseApplication.appContext,"VERSION_CODE"))
        updateUtil.setDebug(true)
        val updateModel = UpdateModel()
        updateModel.isForce = true
        updateModel.title = "StevenAndroid"
        updateModel.versionName = "1.0.0"
        updateModel.versionCode = 100
        updateModel.appName = "StevenAndroid.apk"
        updateModel.downloadUrl =
            "https://myunonline-xiyue.oss-cn-hangzhou.aliyuncs.com/package_sc/xy-marketlegusign400.apk"
        updateModel.message = "1.优化内容.....\n2.优化功能"
        updateModel.positiveStr = "立即升级"
        updateModel.negativeStr = "下次再说"
        updateModel.appId = "com.hx.componentization"
        updateModel.fileMd5 = "F3D5CBCFB4A8F1BC902C6CFD3168D797"
        updateModel.showType = UpdateUtil.TYPE_SIMPLE
        Logger.d(JsonHelp.toJson(updateModel))
        updateUtil.showUpdateDialog(this, updateModel,
            AppUtils.getMetaDataInt(BaseApplication.appContext,"VERSION_CODE"),
            BuildConfig.BUILD_TYPE, object : CheckAppVersionListener {
                override fun readyToUpGrade() {
                    Logger.e("readyToUpGrade")
                }

                override fun cancelUpGrade() {
                    Logger.e("cancelUpGrade")
                    openMain()
                }

                override fun noUpGrade() {
                    Logger.e("noUpGrade")
                    openMain()
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        updateUtil.dismissDialog()
    }
}