package com.asto.recyclingbins.mvp.contract

import com.asto.recyclingbins.base.IPresenter
import com.asto.recyclingbins.base.IView
import com.asto.recyclingbins.bean.RecyclingTypeBean

/**
 * 作者 ：nxk
 * 日期 ：2019/12/9
 * 备注 ：投递页-箱门打开进行投递
 */
interface DeliverContract {

    interface View : IView<Presenter> {

        //显示倒计时定时器
        fun showTimer(time : Int = 90)
        //抓拍箱内照片
        fun capturePicture()
    }

    interface Presenter : IPresenter<View> {
        /**
         * 投递完成
         */
        fun deliverFinish(bean : RecyclingTypeBean)

    }
}