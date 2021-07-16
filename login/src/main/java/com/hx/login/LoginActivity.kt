package com.hx.login

import com.alibaba.android.arouter.facade.annotation.Route
import com.hx.steven.activity.BaseActivity
import com.hx.steven.constant.Constants

@Route(path = Constants.A_LOGIN)
class LoginActivity : BaseActivity() {

    override fun initView() {

    }

    override fun getContentId(): Int {
        return R.layout.activity_login
    }
}