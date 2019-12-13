package com.asto.recyclingbins.mvp.contract

import com.asto.recyclingbins.base.IPresenter
import com.asto.recyclingbins.base.IView

/**
 * 作者 ：nxk
 * 日期 ：2019/12/11
 * 备注 ：未识别页
 */
interface NoRecognitionContract {

    interface View : IView<Presenter> {
        //显示倒计时
        fun showTimer(date: Int = 30)
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