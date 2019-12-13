package com.asto.recyclingbins.adapter

import com.asto.recyclingbins.R
import com.asto.recyclingbins.bean.RecyclingTypeBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * 作者 ：nxk
 * 日期 ：2019/12/5
 * 备注 ：主界面回收类型Adapter
 */
class MainAdapter(list: List<RecyclingTypeBean>) : BaseQuickAdapter<RecyclingTypeBean, BaseViewHolder>(R.layout.item_main, list) {
    override fun convert(helper: BaseViewHolder, item: RecyclingTypeBean) {
        //TODO 后期修改
        when (item.img) {
            "1" -> helper.setImageResource(R.id.mImageView, R.drawable.icon_ylp)
            "2" -> helper.setImageResource(R.id.mImageView, R.drawable.icon_zp)
            "3" -> helper.setImageResource(R.id.mImageView, R.drawable.icon_fzw)
            "4" -> helper.setImageResource(R.id.mImageView, R.drawable.icon_js)
        }
        helper.setText(R.id.mNameTv,item.name)
        helper.setText(R.id.mIntegralTv,item.integral.toString() + mContext.getString(R.string.integral) + "/" + mContext.getString(R.string.unit_weight))
    }

}