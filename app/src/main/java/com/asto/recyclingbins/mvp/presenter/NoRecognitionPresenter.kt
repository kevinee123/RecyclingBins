package com.asto.recyclingbins.mvp.presenter

import com.asto.recyclingbins.R
import com.asto.recyclingbins.base.BaseActivity
import com.asto.recyclingbins.bean.EventMessage
import com.asto.recyclingbins.core.Common
import com.asto.recyclingbins.mvp.contract.NoRecognitionContract
import com.asto.recyclingbins.mvp.view.NoRecognitionActivity
import org.greenrobot.eventbus.EventBus

/**
 * 作者 ：nxk
 * 日期 ：2019/12/11
 * 备注 ：未识别页
 */
class NoRecognitionPresenter(override val view: NoRecognitionActivity) : NoRecognitionContract.Presenter {

    override fun finishDeliver() {
        view.loginOut()
    }

    override fun continueDeliver() {
       view.finishAndRemainHowActivity(1)
        EventBus.getDefault().post(EventMessage<String>(Common.EB_CODE_MAIN_SELECT_TYPE))
    }

    override fun showView() {
        BaseActivity.mSpeechSynthesizer?.speak(view.getString(R.string.speak_msg_no_recognition))
        view.showTimer()
    }

}