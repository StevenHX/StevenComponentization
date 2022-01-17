package com.hx.steven.component.sheetDialog

import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.hx.steven.R
import com.hx.steven.component.DividerItemDecoration
import com.orhanobut.logger.Logger

/**
 * 自定义列表显示内容   sheet Dialog
 */
class CusItemSheetDialog<VH : BaseViewHolder>  : SheetDialogFragment() {

    private lateinit var detailRecycle: RecyclerView
    private lateinit var sheetAdapter: RecyclerView.Adapter<VH>

    override fun getLayoutRes(): Int {
        return R.layout.more_sheet_dialog
    }

    override fun initView(view: View) {
        detailRecycle = view.findViewById(R.id.more_sheet_dialog_recycle)
        detailRecycle.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        detailRecycle.addItemDecoration(DividerItemDecoration().setDrawOuterBorder(true))
        detailRecycle.adapter = sheetAdapter
    }

    override fun getHeightPresent(): Float {
        return 0.8f
    }

    fun showDialog(tran: FragmentManager,tag: String, adapter: RecyclerView.Adapter<VH>) {
        this.sheetAdapter = adapter
        show(tran,tag)
    }

}