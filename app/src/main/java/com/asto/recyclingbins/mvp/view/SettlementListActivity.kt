package com.asto.recyclingbins.mvp.view

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.asto.recyclingbins.R
import com.asto.recyclingbins.adapter.RecognitionAdapter
import com.asto.recyclingbins.adapter.SettlementListAdapter
import com.asto.recyclingbins.base.BaseActivity
import com.asto.recyclingbins.bean.RecyclingTypeBean
import com.asto.recyclingbins.mvp.contract.SettlementListContract
import com.asto.recyclingbins.mvp.presenter.SettlementListPresenter
import com.uuzuche.lib_zxing.activity.CodeUtils
import kotlinx.android.synthetic.main.activity_deliver.*
import kotlinx.android.synthetic.main.activity_settlement_list.*
import kotlinx.android.synthetic.main.activity_settlement_list.mMsgTv
import java.text.DecimalFormat
import java.util.*

/**
 * 作者 ：nxk
 * 日期 ：2019/12/11
 * 备注 ：结算页
 */
class SettlementListActivity :BaseActivity(),SettlementListContract.View {

    override val layoutId = R.layout.activity_settlement_list
    override val presenter = SettlementListPresenter(this)
    lateinit var settlementListAdapter : SettlementListAdapter

    var timerDate = 60
    var timer : Timer? = null

    override fun initData() {
        presenter.showView()
    }

    override fun bindinOnClickListener() {
        mFinishTv.setOnClickListener {
            presenter.finishDeliver()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if(timerDate>0){
            timer?.cancel()
        }
    }

    @SuppressLint("SetTextI18n", "NewApi")
    override fun showTimer(time : Int) {
        timerDate = time
        timer = kotlin.concurrent.timer("结算页定时器", false, 0, 1000) {
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

    override fun showList(list : List<RecyclingTypeBean>) {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        settlementListAdapter = SettlementListAdapter(list)
        mRecyclerView.adapter = settlementListAdapter
    }

    override fun integralTotal(list : List<RecyclingTypeBean>) {
        var integralTotal = 0.0
        list.forEach {
            integralTotal+= it.integral * it.weight
        }
        val defaultDf = DecimalFormat("#0.00")
        mIntegralTotalTv.text = defaultDf.format(integralTotal)
    }

    override fun showQRCode(url :String) {
        val mBitmap = CodeUtils.createImage(url, 150, 150, BitmapFactory.decodeResource(resources, R.drawable.icon_91))
        mQRCodeIv.setImageBitmap(mBitmap)
    }

}