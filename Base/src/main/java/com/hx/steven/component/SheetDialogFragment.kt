package com.hx.steven.component

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hx.steven.R

abstract class SheetDialogFragment : BottomSheetDialogFragment() {
    private var mBehavior: BottomSheetBehavior<*>? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view = View.inflate(context, getLayoutRes(), null)
        dialog.setContentView(view)
        initView(view)
        mBehavior = BottomSheetBehavior.from(view.parent as View)
        val layoutParams = view.layoutParams
        val height = (dialog.context.resources.displayMetrics.heightPixels * 0.8).toInt()
        layoutParams.height = height
        view.layoutParams = layoutParams
        mBehavior?.peekHeight = height
        dialog.window!!.findViewById<View>(R.id.design_bottom_sheet)
            .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onStart() {
        super.onStart()
        //默认全屏展开
        mBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }

    /**
     * 隐藏dialog
     */
    fun doHide() {
        mBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
    }

    /**
     * 获取dialog布局的内容
     */
    abstract fun getLayoutRes(): Int

    /**
     * 初始化view
     */
    abstract fun initView(view: View)
}
