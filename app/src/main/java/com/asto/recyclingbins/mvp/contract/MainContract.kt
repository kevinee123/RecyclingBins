package com.asto.recyclingbins.mvp.contract

import com.asto.recyclingbins.base.IPresenter
import com.asto.recyclingbins.base.IView
import com.asto.recyclingbins.bean.EventMessage
import com.asto.recyclingbins.bean.RecyclingTypeBean
import com.asto.recyclingbins.bean.SerialPortBean

/**
 * 作者 ：nxk
 * 日期 ：2019/12/6
 * 备注 ：主页接口
 */

interface MainContract {
    interface View : IView<Presenter>{
        //显示二维码
        fun showQRCode(url: String)
        /**
         * 显示回收类型
         * @param list : 回收类型集合
         */
        fun showRecyclingType(list: List<RecyclingTypeBean>)
        //显示设备编码
        fun showEquipmentCoding()
        //显示全国服务热线
        fun showMobilePhone()
        //显示箱子异常状态
        fun showRecycingBinsStatus(eventMessage : EventMessage<String>): Boolean
        //显示倒计时定时器
        fun showTimer(eventMessage : EventMessage<String>)
    }
    interface Presenter : IPresenter<View>{
        /**
         * 选择完回收类型去进行登录
         * @param position : 回收类型下标
         */
        fun selectedTypeToLogin(position : Int)
        /**
         * 选择完回收类型去进行回收
         */
        fun selectedTypeToRecycle(position : Int)
    }
}