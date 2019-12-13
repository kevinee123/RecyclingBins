package com.asto.recyclingbins.mvp.presenter

import com.asto.recyclingbins.base.BaseActivity
import com.asto.recyclingbins.bean.EventMessage
import com.asto.recyclingbins.bean.RecyclingTypeBean
import com.asto.recyclingbins.core.Common
import com.asto.recyclingbins.mvp.contract.RecognitionContract
import com.asto.recyclingbins.mvp.view.MainActivity
import com.asto.recyclingbins.mvp.view.RecognitionActivity
import com.asto.recyclingbins.mvp.view.SettlementListActivity
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity

/**
 * 作者 ：nxk
 * 日期 ：2019/12/9
 * 备注 ：识别页
 */
class RecognitionPresenter(override val view: RecognitionActivity) : RecognitionContract.Presenter {

    override fun showView() {
        var list = ArrayList<RecyclingTypeBean>()
        BaseActivity.list?.forEach {
            if (it.weight > 0){
                list.add(it)
            }
        }
        view.showDeliverList(list)
        view.showTimer()
    }

    override fun finishDeliver() {
        view.startActivity<SettlementListActivity>()
    }

    /**
     * 继续投递
     */
    override fun continueDeliver() {
        view.finishAndRemainHowActivity(1)
        EventBus.getDefault().post(EventMessage<String>(Common.EB_CODE_MAIN_SELECT_TYPE))
    }


}