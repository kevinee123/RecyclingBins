package com.asto.recyclingbins.mvp.presenter

import com.asto.recyclingbins.bean.RecyclingTypeBean
import com.asto.recyclingbins.mvp.contract.MainContract
import com.asto.recyclingbins.mvp.view.DeliverActivity
import com.asto.recyclingbins.mvp.view.LoginActivity
import com.asto.recyclingbins.mvp.view.MainActivity
import org.jetbrains.anko.startActivity

/**
 * 作者 ：nxk
 * 日期 ：2019/12/6
 * 备注 ：主页逻辑
 */
class MainPresenter(override val view: MainActivity) : MainContract.Presenter {
    var mRecyclingTypeBeanlist :List<RecyclingTypeBean>? = null
    override fun selectedTypeToLogin(position: Int) {
        //TODO 到时候替换成model返回的数据
        val recyclingTypeBean = mRecyclingTypeBeanlist?.get(position)
        view.startActivity<LoginActivity>("bean" to recyclingTypeBean,"list" to mRecyclingTypeBeanlist)
    }

    override fun selectedTypeToRecycle(position : Int) {
        //TODO 到时候替换成model返回的数据
        val recyclingTypeBean = mRecyclingTypeBeanlist?.get(position)
        view.startActivity<DeliverActivity>("bean" to recyclingTypeBean)
    }

    override fun showView() {
        //TODO 获取MODEL
        mRecyclingTypeBeanlist = getTestRecyclingTypeList()
        view.showEquipmentCoding() //显示机器码
        view.showMobilePhone() //显示客服电话
        view.showQRCode("諾克薩斯之手") //显示二维码
        view.showRecyclingType(mRecyclingTypeBeanlist!!) //显示回收类型
    }

    /**
     * TODO 主界面测试回收类型，后期删除
     */
    private fun getTestRecyclingTypeList(): List<RecyclingTypeBean> {
        val list = ArrayList<RecyclingTypeBean>()
        val recyclingTypeBean = RecyclingTypeBean("饮料瓶", "1", 50)
        list.add(recyclingTypeBean)
        val recyclingTypeBean2 = RecyclingTypeBean("纸品", "2", 70)
        list.add(recyclingTypeBean2)
        val recyclingTypeBean3 = RecyclingTypeBean("纺织物", "3", 20)
        list.add(recyclingTypeBean3)
        val recyclingTypeBean4 = RecyclingTypeBean("金属", "4", 50)
        list.add(recyclingTypeBean4)
        return list
    }

}