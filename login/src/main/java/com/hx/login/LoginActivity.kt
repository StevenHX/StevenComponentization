package com.hx.login

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.appcompat.widget.AppCompatImageView
import com.alibaba.android.arouter.facade.annotation.Route
import com.hx.steven.activity.BaseActivity
import com.hx.steven.component.ClearEditText
import com.hx.steven.constant.Constants
import kotlin.math.roundToInt


@Route(path = Constants.A_LOGIN)
class LoginActivity : BaseActivity() {
    private lateinit var bgIv: AppCompatImageView
    private lateinit var nameEt: ClearEditText
    private lateinit var pwdEt: ClearEditText

    override fun initView() {
        bgIv = findViewById(R.id.login_bg_iv)
        nameEt = findViewById(R.id.login_name)
        pwdEt = findViewById(R.id.login_pwd)
        val blurBmp = blur(this, BitmapFactory.decodeResource(resources, R.drawable.loginbg))
        bgIv.setImageBitmap(blurBmp)

    }

    override fun getContentId(): Int {
        return R.layout.activity_login
    }


    /**
     * 图片缩放比例
     */
    private val BITMAP_SCALE = 0.4f

    /**
     * 最大模糊度(在0.0到25.0之间)
     */
    private val BLUR_RADIUS = 20f

    /**
     * 模糊图片的具体方法
     *
     * @param context   上下文对象
     * @param image     需要模糊的图片
     * @return          模糊处理后的图片
     */
    private fun blur(context: Context?, image: Bitmap): Bitmap? {
        // 计算图片缩小后的长宽
        val width = (image.width * BITMAP_SCALE).roundToInt()
        val height = (image.height * BITMAP_SCALE).roundToInt()

        // 将缩小后的图片做为预渲染的图片。
        val inputBitmap = Bitmap.createScaledBitmap(image, width, height, false)
        // 创建一张渲染后的输出图片。
        val outputBitmap = Bitmap.createBitmap(inputBitmap)

        // 创建RenderScript内核对象
        val rs = RenderScript.create(context)
        // 创建一个模糊效果的RenderScript的工具对象
        val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

        // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间。
        // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去。
        val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
        val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)

        // 设置渲染的模糊程度, 25f是最大模糊度
        blurScript.setRadius(BLUR_RADIUS)
        // 设置blurScript对象的输入内存
        blurScript.setInput(tmpIn)
        // 将输出数据保存到输出内存中
        blurScript.forEach(tmpOut)

        // 将数据填充到Allocation中
        tmpOut.copyTo(outputBitmap)
        return outputBitmap
    }
}