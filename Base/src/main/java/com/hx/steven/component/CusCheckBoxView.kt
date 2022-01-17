package com.hx.steven.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.hx.steven.R
import com.orhanobut.logger.Logger

class CusCheckBoxView : LinearLayoutCompat {
    /**
     * 标题
     */
    private lateinit var titleTv: AppCompatTextView

    /**
     * checkBox
     */
    private lateinit var checkBox: AppCompatCheckBox

    private var listener: CheckChangeListener? = null

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) {
        val a =
            context!!.obtainStyledAttributes(attrs, R.styleable.CusCheckBoxView, defStyleAttr, 0)
        val titleStr = a.getString(R.styleable.CusCheckBoxView_cb_title)
        val initCheck = a.getBoolean(R.styleable.CusCheckBoxView_cb_initCheck, false)
        a.recycle()

        val view: View = LayoutInflater.from(context).inflate(R.layout.cus_checkbox_view, null)
        addView(view)

        titleTv = view.findViewById(R.id.cus_cb_title)
        checkBox = view.findViewById(R.id.cus_cb_checkbox)

        titleTv.text = titleStr
        checkBox.isChecked = initCheck

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            listener?.onCheckChangeListener(this, isChecked)
        }
    }


    interface CheckChangeListener {
        fun onCheckChangeListener(v: View, isCheck: Boolean)
    }

    fun setOnCheckChangeListener(listener: CheckChangeListener) {
        this.listener = listener
    }

    /**
     * 获取checkBox是否选中
     */
    fun getIsChecked(): Boolean {
        return checkBox.isChecked
    }

    /**
     * 设置checkBox
     */
    fun setIsChecked(isCheck: Boolean) {
        checkBox.isChecked = isCheck
    }

    fun setCheckEnable(enable:Boolean) {
        checkBox.isEnabled = enable
    }

    fun getCheckIsEnable(): Boolean {
        return checkBox.isEnabled
    }

}