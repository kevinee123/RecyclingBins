package com.asto.recyclingbins.mvp.view

import android.annotation.SuppressLint
import androidx.recyclerview.widget.LinearLayoutManager
import com.asto.recyclingbins.R
import com.asto.recyclingbins.adapter.RecognitionAdapter
import com.asto.recyclingbins.base.BaseActivity
import com.asto.recyclingbins.bean.RecyclingTypeBean
import com.asto.recyclingbins.core.Common
import com.asto.recyclingbins.mvp.contract.RecognitionContract
import com.asto.recyclingbins.mvp.presenter.RecognitionPresenter
import kotlinx.android.synthetic.main.activity_recognition.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity
import java.util.*

/**
 * 作者 ：nxk
 * 日期 ：2019/12/9
 * 备注 ：识别页
 */
class RecognitionActivity : BaseActivity(),RecognitionContract.View {
    override val layoutId = R.layout.activity_recognition
    override val presenter = RecognitionPresenter(this)

    var timerDate = 60
    var timer : Timer? = null
   lateinit var recognitionAdapter : RecognitionAdapter

    override fun initData() {
        presenter.showView()
    }

    override fun bindinOnClickListener() {
        mFinishDeliverTv.setOnClickListener {
            //结束投递 TODO 跳转结算页
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

    override fun showDeliverList(list : List<RecyclingTypeBean>) {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        recognitionAdapter = RecognitionAdapter(list)
        mRecyclerView.adapter = recognitionAdapter
    }

    @SuppressLint("SetTextI18n", "NewApi")
    override fun showTimer(time : Int) {
        timerDate = time
        timer = kotlin.concurrent.timer("识别页定时器", false, 0, 1000) {
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