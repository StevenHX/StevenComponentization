package com.hx.steven.component

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hx.steven.R
import com.hx.steven.util.UnitConverter
import java.lang.reflect.Method
import java.util.regex.Pattern

/**
 * 自定义输入view
 */
class CusInputView : LinearLayoutCompat {
    /**
     * 标题
     */
    private lateinit var titleTv: AppCompatTextView

    /**
     * 输入框
     */
    private lateinit var valueEt: AppCompatEditText

    /**
     * inputLayout
     */
    private lateinit var valueLayout: TextInputLayout

    private var listener: CusInputListener? = null

    private var mClearDrawable: Drawable? = null
    private var hasFocus = false

    /**
     * 标题文本
     */
    private lateinit var titleStr: String

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context!!.obtainStyledAttributes(attrs, R.styleable.CusInputView, defStyleAttr, 0)
        val type = a.getString(R.styleable.CusInputView_type)
        titleStr = a.getString(R.styleable.CusInputView_title).toString()
        val hintStr = a.getString(R.styleable.CusInputView_hint)
        val isNeedCheck = a.getBoolean(R.styleable.CusInputView_isNeedCheck, false)
        val isDisableKeyBoard = a.getBoolean(R.styleable.CusInputView_isDisableKeyBoard, false)
        val patternStr = a.getString(R.styleable.CusInputView_pattern)
        val patternMsg = a.getString(R.styleable.CusInputView_pattern_msg)
        val inputType = a.getInt(R.styleable.CusInputView_inputType, InputType.TYPE_NULL)
        a.recycle()
        var view: View? = null
        if (type == "0" || type == null) {
            view = LayoutInflater.from(context).inflate(R.layout.cus_inputview, null)
            addView(view)
        } else if (type == "1") {
            view = LayoutInflater.from(context).inflate(R.layout.cus_input_info_view, null)
            val params: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            view.layoutParams = params
            addView(view)
        }
        titleTv = view?.findViewById(R.id.cus_input_title)!!
        valueEt = view.findViewById(R.id.cus_input_et)
        valueLayout = view.findViewById(R.id.cus_input_layout)

        titleTv.text = titleStr
        valueEt.hint = hintStr
        valueEt.inputType = inputType
        if (type == "1") return
        // 判断是否需要检查
        if (!isNeedCheck) {
            titleTv.text = "$titleStr(选填)"
        } else {
            val requireDraw = ContextCompat.getDrawable(context, R.drawable.required)
            requireDraw?.setBounds(0, 0, UnitConverter.dpToPx(16), UnitConverter.dpToPx(16))
            titleTv.setCompoundDrawables(null, null, requireDraw, null)
        }
        // 正则
        var pattern: Pattern? = null
        patternStr?.let { pattern = Pattern.compile(it) }
        // 设置监听
        valueLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (hasFocus) {
                    setClearIconVisible(s!!.isNotEmpty())
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (isNeedCheck) checkValueValidate(pattern, s.toString(), titleStr, patternMsg)
            }
        })
        valueEt.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            listener?.onFocusCallBack(v, hasFocus)
            if (isNeedCheck) checkValueValidate(
                pattern,
                (v as AppCompatEditText).text.toString(),
                titleStr,
                patternMsg
            )
            this.hasFocus = hasFocus
            if (hasFocus) {
                valueEt.text?.let { setClearIconVisible(it.isNotEmpty()) }
            } else {
                setClearIconVisible(false)
            }
        }

        initClearDrawable()
        if (isDisableKeyBoard) {
            setDisableKeyBoard()
            valueEt.clearFocus()
        }
    }

    interface CusInputListener {
        fun onCheckResult(view: View, isRight: Boolean, value: String)
        fun onFocusCallBack(view: View, hasFocus: Boolean)
    }

    fun setOnCheckListener(listener: CusInputListener) {
        this.listener = listener
    }

    /**
     * 校验数据合法性
     */
    fun checkValueValidate(pattern: Pattern?, s: String, titleStr: String?, patternMsg: String?) {
        if (s.isEmpty()) {
            valueLayout.isErrorEnabled = true
            valueLayout.error = titleStr + "不能为空"
            listener?.onCheckResult(this@CusInputView, false, s)
            return
        } else {
            valueLayout.isErrorEnabled = false
            if (pattern == null) {
                listener?.onCheckResult(this@CusInputView, true, s)
            }
        }
        pattern?.let {
            val isMatch = it.matcher(s).matches()
            if (!isMatch) {
                valueLayout.isErrorEnabled = true
                valueLayout.error = patternMsg
                listener?.onCheckResult(this@CusInputView, false, s)
            } else {
                valueLayout.isErrorEnabled = false
                listener?.onCheckResult(this@CusInputView, true, s)
            }
        }
    }

    /**
     * 获取数据
     */
    fun getInputValue(): String {
        return valueEt.text.toString()
    }

    /**
     * 设置显示信息
     * @param type 0 失败 1 成功 2 清空
     * @param textStr 显示信息字符串
     */
    fun setInfoText(type: Int, textStr: String) {
        valueEt.text = Editable.Factory.getInstance().newEditable(textStr)
        valueEt.setTextColor(Color.WHITE)
        when (type) {
            0 -> {
                valueEt.background = ColorDrawable(ContextCompat.getColor(context, R.color.darkred))
            }
            1 -> {
                valueEt.background =
                    ColorDrawable(ContextCompat.getColor(context, R.color.mediumseagreen))
            }
            2 -> {
                valueEt.background = ColorDrawable(ContextCompat.getColor(context, R.color.white))
            }
        }
    }

    /**
     * 设置普通输入框值
     */
    fun setCusText(textStr: String) {
        valueEt.text = Editable.Factory.getInstance().newEditable(textStr)
    }

    /**
     * 设置输入禁止弹出键盘
     */
    fun setDisableKeyBoard() {
        val cls: Class<EditText> = EditText::class.java
        var method: Method
        try {
            method = cls.getMethod("setShowSoftInputOnFocus", Boolean::class.java)
            method.isAccessible = true
            method.invoke(valueEt, false)
        } catch (e: Exception) {
        }
        try {
            method = cls.getMethod("setSoftInputShownOnFocus", Boolean::class.java)
            method.isAccessible = true
            method.invoke(valueEt, false)
        } catch (e: Exception) {
        }
    }

    /**
     * 隐藏键盘
     */
    fun hideKeyBoard(context: Activity) {
        val imm: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager;
        if (imm.isActive && context.currentFocus != null) {
            if (context.currentFocus!!.windowToken != null) {
                imm.hideSoftInputFromWindow(
                    context.currentFocus!!.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                );
            }
        }
    }

    /**
     * 设置清除按钮
     */
    fun initClearDrawable() {
        //获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        mClearDrawable = valueEt.compoundDrawables[2]
        if (mClearDrawable == null) {
            mClearDrawable = ContextCompat.getDrawable(context, R.drawable.input_del)
        }
        mClearDrawable?.intrinsicHeight?.let {
            mClearDrawable?.setBounds(
                UnitConverter.spToPx(5),
                0,
                mClearDrawable!!.intrinsicWidth + UnitConverter.spToPx(5),
                it
            )
        }
        //默认设置隐藏图标
        setClearIconVisible(false)
        valueEt.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (valueEt.compoundDrawables[2] != null) {
                    val touchable =
                        (event.x > (width - valueEt.totalPaddingRight - UnitConverter.dpToPx(30))
                                && event.x < (width - valueEt.paddingRight - UnitConverter.dpToPx(15)))
                    if (touchable) {
                        valueEt.setText("")
                    }
                }
            }
            false
        }
    }

    /**
     * 设置清除图标显示隐藏
     */
    fun setClearIconVisible(visible: Boolean) {
        val right = if (visible) mClearDrawable else null
        valueEt.setCompoundDrawables(
            valueEt.compoundDrawables[0],
            valueEt.compoundDrawables[1], right, valueEt.compoundDrawables[3]
        )
    }

    /**
     * 设置输入框禁止编辑
     */
    fun setInputEditable(isEnable: Boolean) {
        valueEt.isEnabled = isEnable
    }

    /**
     * 设置自定义标题
     */
    fun setCusTitle(str: String) {
        titleStr = str
        titleTv.text = str
        valueEt.hint = "请输入$str"
    }
}