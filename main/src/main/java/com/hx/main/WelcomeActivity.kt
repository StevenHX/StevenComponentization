package com.hx.main

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hx.steven.activity.BaseActivity
import com.hx.steven.component.SuperTextView
import com.hx.steven.constant.Constants
import com.orhanobut.logger.Logger
import com.steven.updatetool.CheckAppVersionListener
import com.steven.updatetool.UpdateModel
import com.steven.updatetool.UpdateUtil

@Route(path = Constants.A_WELCOME)
class WelcomeActivity : BaseActivity() {
    private lateinit var tvName: SuperTextView
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
        val updateUtil = UpdateUtil()
        updateUtil.setDebug(true)
        val updateModel = UpdateModel()
        updateModel.isForce = false
        updateModel.title = "喜阅商城"
        updateModel.versionName = "4.0.0"
        updateModel.versionCode = 400
        updateModel.appName = "xyMall.apk"
        updateModel.downloadUrl = "https://myunonline-xiyue.oss-cn-hangzhou.aliyuncs.com/package_sc/xy-marketlegusign400.apk"
        updateModel.message = "1.优化内容.....\n2.优化功能"
        updateModel.positiveStr = "立即升级"
        updateModel.negativeStr = "下次再说"
        updateModel.appId = "1212"
        updateModel.imgSrc = R.drawable.top_bg
        updateModel.bottomBg = R.drawable.btn_bg
        updateModel.fileMd5 = "F3D5CBCFB4A8F1BC902C6CFD3168D797"
        updateUtil.showUpdateDialog(this, updateModel, 400,
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
}