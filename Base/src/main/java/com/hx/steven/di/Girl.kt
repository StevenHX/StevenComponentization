package com.hx.steven.di

import android.util.Log

class Girl {
    var userName: String? = null
    var age: Int? = null
    fun info() {
        Log.i("girl", "用户名:" + userName + "////年龄:" + age)
    }
}