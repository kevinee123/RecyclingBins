package com.asto.recyclingbins.mvp.contract

import com.asto.recyclingbins.base.IPresenter
import com.asto.recyclingbins.base.IView

/**
 * 作者 ：nxk
 * 日期 ：2019/12/7
 * 备注 ：扫码登录界面
 */
interface LoginContract {
    interface View : IView<Presenter> {
        /**
         * 显示二维码
         * @param url : 二维码生成的内容
         */
        fun showQRCode(url: String)

        //显示倒计时定时器
        fun showTimer(time : Int = 60)
    }

    interface Presenter : IPresenter<View> {
        /**
         * TODO 通过通知进行登录
         */
        fun login()
    }
}