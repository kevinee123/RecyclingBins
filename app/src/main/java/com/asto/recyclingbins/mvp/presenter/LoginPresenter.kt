package com.asto.recyclingbins.mvp.presenter

import com.asto.recyclingbins.R
import com.asto.recyclingbins.base.BaseActivity
import com.asto.recyclingbins.base.BaseActivity.Companion.list
import com.asto.recyclingbins.bean.RecyclingTypeBean
import com.asto.recyclingbins.mvp.contract.LoginContract
import com.asto.recyclingbins.mvp.view.DeliverActivity
import com.asto.recyclingbins.mvp.view.LoginActivity
import com.asto.recyclingbins.mvp.view.MainActivity
import org.jetbrains.anko.startActivity

/**
 * 作者 ：nxk
 * 日期 ：2019/12/7
 * 备注 ：扫码登录界面
 */
class LoginPresenter(override val view: LoginActivity) : LoginContract.Presenter{

    override fun login() {
        //TODO 登录成功把isLogin设为true
        BaseActivity.isLogin = true
        list = (view.intent.extras?.getSerializable("list")?:ArrayList<RecyclingTypeBean>()) as List<RecyclingTypeBean>
        view.finish()
        view.startActivity<DeliverActivity>("bean" to view.intent.extras?.getSerializable("bean"))
    }

    override fun showView() {
        BaseActivity.mSpeechSynthesizer?.speak(view.getString(R.string.speak_msg_pls_login))
        view.showQRCode("啦啦啦啦德玛西亚")//TODO 后期修改为唯一登陆码
        view.showTimer()
    }

}