package com.hx.main

import android.view.View
import android.widget.TextView
import com.hx.steven.component.SheetDialogFragment
import com.orhanobut.logger.Logger

class MySheetDialog: SheetDialogFragment() {
    override fun getLayoutRes(): Int {
        return R.layout.dialog_bottom_sheet
    }

    override fun initView(view: View) {
        var tvView: TextView = view.findViewById<TextView>(R.id.xxx)
        Logger.d(tvView.text)
    }

}