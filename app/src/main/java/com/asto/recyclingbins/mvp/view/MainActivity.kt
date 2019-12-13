package com.asto.recyclingbins.mvp.view

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.asto.recyclingbins.R
import com.asto.recyclingbins.adapter.MainAdapter
import com.asto.recyclingbins.base.BaseActivity
import com.asto.recyclingbins.bean.EventMessage
import com.asto.recyclingbins.bean.RecyclingTypeBean
import com.asto.recyclingbins.core.Common
import com.asto.recyclingbins.core.getSerialNumber
import com.asto.recyclingbins.mvp.contract.MainContract
import com.asto.recyclingbins.mvp.presenter.MainPresenter
import com.uuzuche.lib_zxing.activity.CodeUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.concurrent.timer

/**
 * 作者 ：nxk
 * 日期 ：2019/11/28
 * 备注 ：主界面
 */
class MainActivity : BaseActivity(), MainContract.View {
    override val layoutId = R.layout.activity_main

    override val presenter = MainPresenter(this)

    lateinit var mainAdapter: MainAdapter
    var timerDate = 60
    var timer: Timer? = null


    override fun initData() {
        EventBus.getDefault().register(this)
        presenter.showView()
    }

    override fun bindinOnClickListener() {
        mainAdapter.setOnItemClickListener { adapter, view, position ->
            //TODO 回收类型点击事件
            timer?.cancel()
            timerDate = 60
            if (!isLogin) presenter.selectedTypeToLogin(position) //登录账户
            else presenter.selectedTypeToRecycle(position) //已登录跳转投递页
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
    }

    /**
     * 显示倒计时定时器
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun showTimer(eventMessage: EventMessage<String>) {
        if (eventMessage.code == Common.EB_CODE_MAIN_SELECT_TYPE) {
            if (isLogin) {
                mMsgLl.visibility = View.VISIBLE
                mMsgIv.setImageResource(R.drawable.icon_timer)
                mMsgTv.setTextColor(resources.getColor(R.color.orange_E44D26))
                timer = timer("主界面定时器", false, 0, 1000) {
                    runOnUiThread {
                        mMsgTv.text = timerDate.toString() + "S"
                    }
                    timerDate--
                    if (timerDate <= 0) {
                        this.cancel()
                        runOnUiThread {
                            mMsgLl.visibility = View.INVISIBLE
                        }
                        loginOut()
                    }
                }
            }
        } else if (eventMessage.code == Common.EB_CODE_MAIN_FINISH) {
            timer?.cancel()
            mMsgLl.visibility = View.INVISIBLE
        }
    }

    /**
     * 生成二维码
     */
    override fun showQRCode(url: String) {
        val mBitmap = CodeUtils.createImage(url, 150, 150, BitmapFactory.decodeResource(resources, R.drawable.icon_91))
        mQRCodeIv.setImageBitmap(mBitmap)
    }

    /**
     * 显示回收类型
     */
    override fun showRecyclingType(list: List<RecyclingTypeBean>) {
        //初始化RecyclerView
        mRecyclerView.layoutManager = GridLayoutManager(this, 2)
        mainAdapter = MainAdapter(list)
        mRecyclerView.adapter = mainAdapter
    }

    /**
     * 显示机器码
     */
    override fun showEquipmentCoding() {
        mDeviceNoTv.text = getString(R.string.device_no).format(getSerialNumber())
    }

    /**
     * 显示服务热线
     */
    override fun showMobilePhone() {
        //TODO 后期修改服务热线
        mPhoneTv.text = getString(R.string.service_hotline).format("18546857748")
    }

    /**
     * 显示回收箱状态
     * return : true 状态正常 false 状态异常
     */
    @SuppressLint("NewApi")
    @Subscribe
    override fun showRecycingBinsStatus(eventMessage: EventMessage<String>): Boolean {
        //TODO 等厂家技术回复
        //回收箱状态
        if (eventMessage.code == Common.SERIAL_PORT_TYPE_INFO) {
            //如果异常
            if (isLogin) {
//                isLogin = false
//                list = null
            }
            //            mMsgLl.visibility = View.VISIBLE
            //            mMsgIv.setImageResource(R.drawable.icon_error)
            //            mMsgTv.setTextColor(getColor(R.color.red_f00000))
        }
        return true
    }

}