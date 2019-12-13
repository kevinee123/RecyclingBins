package com.asto.recyclingbins.mvp.presenter

import com.asto.recyclingbins.base.BaseActivity
import com.asto.recyclingbins.bean.RecyclingTypeBean
import com.asto.recyclingbins.mvp.contract.SettlementListContract
import com.asto.recyclingbins.mvp.view.SettlementListActivity

/**
 * 作者 ：nxk
 * 日期 ：2019/12/11
 * 备注 ：结算页
 */
class SettlementListPresenter(override val view: SettlementListActivity) : SettlementListContract.Presenter {

    override fun finishDeliver() {
        view.loginOut(true)
    }

    override fun showView() {
        view.showTimer()
        var list = ArrayList<RecyclingTypeBean>()
        if (BaseActivity.list != null) {
            BaseActivity.list?.forEach {
                if (it.weight > 0) {
                    list.add(it)
                }
            }
            view.showList(list)
            view.integralTotal(list)
            view.showQRCode("小程序")
        }
    }

}