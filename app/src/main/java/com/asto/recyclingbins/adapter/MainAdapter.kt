package com.asto.recyclingbins.adapter

import com.asto.recyclingbins.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainAdapter(list : List<String>) : BaseQuickAdapter<String,BaseViewHolder>(R.layout.item_main,list){

    override fun convert(helper: BaseViewHolder, item: String) {
        helper.setText(R.id.mButton,item)
        helper.addOnClickListener(R.id.mButton)
    }

}