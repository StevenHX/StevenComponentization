package com.hx.steven.component.sheetDialog

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.hx.steven.R

class MoreSheetAdapter(
    layoutResId: Int,
    data: MutableList<SheetItemBean>?
) :
    BaseQuickAdapter<SheetItemBean, BaseViewHolder>(layoutResId, data) {
    override fun convert(holder: BaseViewHolder, item: SheetItemBean) {
            holder.setText(R.id.sheet_item_title,item.title)
            holder.setText(R.id.sheet_item_value,item.value)
    }
}