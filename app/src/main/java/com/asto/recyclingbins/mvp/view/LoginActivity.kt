package com.asto.recyclingbins.mvp.view

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.View
import com.asto.recyclingbins.R
import com.asto.recyclingbins.base.BaseActivity
import com.asto.recyclingbins.mvp.contract.LoginContract
import com.asto.recyclingbins.mvp.presenter.LoginPresenter
import com.uuzuche.lib_zxing.activity.CodeUtils
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

/**
 * 作者 ：nxk
 * 日期 ：2019/12/7
 * 备注 ：扫码登录界面
 */
class LoginActivity : BaseActivity(),LoginContract.View{
    override val layoutId: Int
        get() = R.layout.activity_login

    override val presenter = LoginPresenter(this)

    var timerDate = 60
    var timer : Timer? = null

    override fun initData() {
        presenter.showView()
    }

    override fun bindinOnClickListener() {
        //TODO 测试 后期为扫码登录
        mQRCodeIv.setOnClickListener {
            presenter.login()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(timerDate>0){
            timer?.cancel()
        }
    }

    override fun showQRCode(url: String) {
        val mBitmap = CodeUtils.createImage(url, 150, 150, BitmapFactory.decodeResource(resources, R.drawable.icon_91))
        mQRCodeIv.setImageBitmap(mBitmap)
    }

    @SuppressLint("SetTextI18n", "NewApi")
    override fun showTimer(time : Int) {
        timerDate = time
        timer = kotlin.concurrent.timer("扫码登录界面定时器", false, 0, 1000) {
            runOnUiThread {
                mMsgTv.text = timerDate.toString() + "S"
            }
            timerDate--
            if (timerDate <= 0){
                loginOut()
                this.cancel()
            }
        }
    }


}