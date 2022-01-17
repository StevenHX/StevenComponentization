package com.hx.steven.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.hx.steven.R
import com.hx.steven.util.UnitConverter

/**
 * 自定义selectView
 */
class CusSelectView : LinearLayoutCompat, View.OnClickListener {
    /**
     * 标题
     */
    private lateinit var titleTv: AppCompatTextView

    /**
     * 数据
     */
    private lateinit var valueTv: AppCompatTextView

    /**
     * 选中值的code
     */
    private var selectCode: String = ""

    /**
     * 是否是dialog模式
     */
    private var isDialogMode = false

    var options1Items: List<String> = listOf()

    private var listener: ClickListener? = null

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CusSelectView, defStyleAttr, 0)
        val titleStr = a.getString(R.styleable.CusSelectView_selectTitle)
        val isQuire = a.getBoolean(R.styleable.CusSelectView_selectIsQuire, true)
        isDialogMode = a.getBoolean(R.styleable.CusSelectView_dialogMode, false)
        a.recycle()
        val view = LayoutInflater.from(context).inflate(R.layout.cus_select_view, null)
        addView(view)

        titleTv = view.findViewById(R.id.cus_select_title)
        valueTv = view.findViewById(R.id.cus_select_value)
        titleTv.text = titleStr
        valueTv.text = "请选择$titleStr"
        valueTv.setOnClickListener(this)

        if (!isQuire) {
            titleTv.text = "$titleStr(选填)"
            return
        }
        val requireDraw = ContextCompat.getDrawable(context, R.drawable.required)
        requireDraw?.setBounds(0, 0, UnitConverter.dpToPx(16), UnitConverter.dpToPx(16))
        titleTv.setCompoundDrawables(null, null, requireDraw, null)
    }

    override fun onClick(v: View?) {
        val pvOptions: OptionsPickerView<String> = OptionsPickerBuilder(
            context
        ) { options1, _, _, _ ->
            val tx: String = options1Items[options1]
            listener?.onSelectClickListener(this, options1, tx)
        }.isDialog(isDialogMode).build<String>()
        pvOptions.setPicker(options1Items)
        pvOptions.show()
    }

    interface ClickListener {
        fun onSelectClickListener(v: View, options1: Int, tx: String)
    }

    fun setOnSelectClickListener(listener: ClickListener) {
        this.listener = listener
    }

    fun setValueTvValue(value: String) {
        this.valueTv.text = value
        this.valueTv.setTextColor(ContextCompat.getColor(context, R.color.black_metrial))
    }

    fun setSelectCode(code: String) {
        this.selectCode = code
    }

    fun getSelectCode(): String {
        return selectCode
    }

    fun getValueTvValue(): String {
        return if (this.valueTv.text.toString().contains("请选择")) {
            ""
        } else {
            this.valueTv.text.toString()
        }
    }

    fun setIsDialogMode(isDialog:Boolean) {
        isDialogMode = isDialog
    }
}