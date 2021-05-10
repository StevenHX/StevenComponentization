package com.hx.main

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hx.steven.activity.BaseActivity
import com.hx.steven.component.SuperTextView
import com.hx.steven.constant.Constants

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
                openMain()
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
}