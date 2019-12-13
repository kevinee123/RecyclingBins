package com.asto.recyclingbins.mvp.contract

import com.asto.recyclingbins.base.IPresenter
import com.asto.recyclingbins.base.IView
import com.asto.recyclingbins.bean.RecyclingTypeBean

/**
 * 作者 ：nxk
 * 日期 ：2019/12/11
 * 备注 ：结算页
 */
interface SettlementListContract {

    interface View : IView<Presenter> {
        /**
         * 倒计时
         */
       fun showTimer(time : Int = 60)
        /**
         * 投递详情
         */
        fun showList(list : List<RecyclingTypeBean>)
        /**
         * 积分合计
         */
        fun integralTotal(list : List<RecyclingTypeBean>)
        /**
         * 显示二维码
         */
        fun showQRCode(url: String)
    }

    interface Presenter : IPresenter<View> {
        /**
         * 投递完成
         */
        fun finishDeliver()
    }
}