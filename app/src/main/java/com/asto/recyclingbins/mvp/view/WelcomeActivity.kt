package com.asto.recyclingbins.mvp.view

import com.asto.recyclingbins.base.BaseActivity

/**
 * 欢迎界面
 */
class WelcomeActivity : BaseActivity(){

    override val layoutId: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun initData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun bindinOnClickListener() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //自动更新
//    override fun setUpDateApp(bean: UpdateBean) {
//        val newVersion = bean.version
//        mVersionTv.text = "版本号：" + getLocalVersionNo(this)
//        if (newVersion > getLocalVersion(this)) {
//            showToast(resources.getString(R.string.update_app))
//            //需要更新
//            val intent = Intent(this, UpdateService::class.java)
//            val bundle = Bundle()
//            bundle.putString("url", bean.apk_url)
//            intent.putExtras(bundle)
//            startService(intent)
//        }
//    }

}
