package com.asto.recyclingbins.mvp.contract

import com.asto.recyclingbins.base.IPresenter
import com.asto.recyclingbins.base.IView
import com.asto.recyclingbins.bean.RecyclingTypeBean

/**
 * 作者 ：nxk
 * 日期 ：2019/12/9
 * 备注 ：识别页
 */
interface RecognitionContract {

    interface View : IView<Presenter> {

        //显示已投递物品清单
        fun showDeliverList(list : List<RecyclingTypeBean>)

        //显示倒计时定时器
        fun showTimer(time : Int = 60)
    }
    interface Presenter : IPresenter<View> {
        /**
         * 结束投递
         */
        fun finishDeliver()

        /**
         * 继续投递
         */
        fun continueDeliver()
    }
}