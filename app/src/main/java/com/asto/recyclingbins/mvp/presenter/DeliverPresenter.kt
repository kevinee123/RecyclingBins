package com.asto.recyclingbins.mvp.presenter

import com.asto.recyclingbins.R
import com.asto.recyclingbins.base.BaseActivity
import com.asto.recyclingbins.bean.RecyclingTypeBean
import com.asto.recyclingbins.mvp.contract.DeliverContract
import com.asto.recyclingbins.mvp.view.DeliverActivity
import com.asto.recyclingbins.mvp.view.NoRecognitionActivity
import com.asto.recyclingbins.mvp.view.RecognitionActivity
import org.jetbrains.anko.startActivity
import java.text.DecimalFormat
import java.util.*
import kotlin.concurrent.timer

/**
 * 作者 ：nxk
 * 日期 ：2019/12/9
 * 备注 ：投递页
 */
class DeliverPresenter(override val view: DeliverActivity) : DeliverContract.Presenter{
    var timer : Timer? = null
    override fun showView() {
        timer = timer("定时读取重量", false, 0, 500){
            BaseActivity.mSerialPortUtils?.doWeigh()//称重
        }
        BaseActivity.mSerialPortUtils?.doInsideLight(true)//打开箱内灯
        BaseActivity.mSerialPortUtils?.doDeliveryDoor(true)//打开投递门
        view.showTimer()
        BaseActivity.mSpeechSynthesizer?.speak(view.getString(R.string.speak_msg_door_open))
    }

    override fun deliverFinish(bean : RecyclingTypeBean) {
        timer?.cancel()
        timer = null
        BaseActivity.mSerialPortUtils?.doDeliveryDoor(false)//关闭投递门
        BaseActivity.mSerialPortUtils?.doInsideLight(false)//关闭箱内灯
        BaseActivity.mSpeechSynthesizer?.speak(view.getString(R.string.speak_msg_door_close))
        if(view.initWeight != null && view.lastWeight > view.initWeight!!){
            //投递成功
            BaseActivity.list?.forEach {
                if(it.name == bean.name){
                    it.weight += view.lastWeight - view.initWeight!!
                    val defaultDf = DecimalFormat("#0.00")
                    it.weight = defaultDf.format(it.weight).toDouble()
                }
            }
            view.startActivity<RecognitionActivity>()
        }else{
            //投递失败
            view.startActivity<NoRecognitionActivity>()
        }
    }

}