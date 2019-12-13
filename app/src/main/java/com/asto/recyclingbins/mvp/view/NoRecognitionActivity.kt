package com.asto.recyclingbins.mvp.view

import com.asto.recyclingbins.R
import com.asto.recyclingbins.base.BaseActivity
import com.asto.recyclingbins.core.Common
import com.asto.recyclingbins.mvp.contract.NoRecognitionContract
import com.asto.recyclingbins.mvp.presenter.NoRecognitionPresenter
import kotlinx.android.synthetic.main.activity_no_recognition.*
import kotlinx.android.synthetic.main.activity_no_recognition.mContinueDeliverTv
import kotlinx.android.synthetic.main.activity_no_recognition.mFinishDeliverTv
import kotlinx.android.synthetic.main.activity_no_recognition.mMsgTv
import kotlinx.android.synthetic.main.activity_recognition.*
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * 作者 ：nxk
 * 日期 ：2019/12/11
 * 备注 ：未识别页
 */
class NoRecognitionActivity : BaseActivity(),NoRecognitionContract.View {

    override val layoutId: Int
        get() = R.layout.activity_no_recognition
    override val presenter = NoRecognitionPresenter(this)

    var timerDate = 30
    var timer : Timer? = null

    override fun initData() {
        presenter.showView()
    }

    override fun bindinOnClickListener() {
        mFinishDeliverTv.setOnClickListener {
            //结束投递
            presenter.finishDeliver()
        }
        mContinueDeliverTv.setOnClickListener {
            //继续投递
            presenter.continueDeliver()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if(timerDate>0){
            timer?.cancel()
        }
    }

    override fun showTimer(time : Int) {
        timerDate = time
        timer = kotlin.concurrent.timer("未识别页定时器", false, 0, 1000) {
            runOnUiThread {
                mMsgTv.text = timerDate.toString() + "S"
            }
            timerDate--
            if (timerDate <= 0){
                this.cancel()
                loginOut()
            }
        }
    }

}