package com.hx.steven.component.sheetDialog

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hx.steven.R
import com.hx.steven.component.DividerItemDecoration
import kotlin.reflect.full.memberProperties

/**
 * 展示更多详细，底部弹出dialog
 * 传入对象，自动根据反射解析属性名称
 */
class MoreSheetDialog<T : BaseSheetBean> : SheetDialogFragment() {
    override fun getLayoutRes(): Int {
        return R.layout.more_sheet_dialog
    }

    private var detailData: MutableList<SheetItemBean> = mutableListOf()
    private lateinit var detailRecycle: RecyclerView
    private lateinit var sheetAdapter: MoreSheetAdapter

    fun setBeanData(bean: T) {
        detailData = mutableListOf()
        val clazz = bean.javaClass.kotlin
        clazz.memberProperties.forEach {
            detailData.add(SheetItemBean(it.name, "" + it.get(bean)))
        }
    }


    override fun initView(view: View) {
        detailRecycle = view.findViewById(R.id.more_sheet_dialog_recycle)
        detailRecycle.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        detailRecycle.addItemDecoration(DividerItemDecoration().setDrawOuterBorder(true))
        sheetAdapter = MoreSheetAdapter(R.layout.more_sheet_item, detailData)
        detailRecycle.adapter = sheetAdapter
    }

    override fun getHeightPresent(): Float {
        return 0.8f
    }
}