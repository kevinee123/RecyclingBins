package com.asto.autoweigh.base

import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.android.tu.loadingdialog.LoadingDialog
import com.asto.autoweigh.core.MyApplication
import com.umeng.analytics.MobclickAgent

abstract class BaseActivity : FragmentActivity(){

    var mLoadingDialog: LoadingDialog? = null
    val application: MyApplication
    get() {
        return getApplication() as MyApplication
    }

    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)

        application.addActivity(this)

        if (mLoadingDialog == null)
            initLoadingDialog()
        initData()
        bindinOnClickListener()
    }

    protected abstract fun initData()


    protected abstract fun bindinOnClickListener()

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }

    override fun onDestroy() {
        application.removeActivity(this)
        super.onDestroy()
    }

    private fun initLoadingDialog() {
        val loadBuilder = LoadingDialog.Builder(this)
            .setMessage("加载中...")
            .setCancelable(true)
            .setCancelOutside(true)
        mLoadingDialog = loadBuilder.create()
    }

    fun showLoading() {
        if (mLoadingDialog == null)
            initLoadingDialog()
        mLoadingDialog!!.show()
    }

    fun dismissLoading() {
        mLoadingDialog!!.dismiss()
    }

    fun showToast(msg: String) {
        val toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }


    /**
     * 关闭Activity直到剩下几层
     *
     * @param remain 剩余Activity数
     */
    fun finishAndRemainHowActivity(remain: Int) {
        val activityList = application.getActivityList()
        var size = activityList.size
        while (size > remain) {
            activityList.get(size - 1).finish()
            size--
        }
    }

    /**
     * 关闭所有Activity
     */
    fun finishAllActivity() {
        val activityList = application.getActivityList()
        for (activity in activityList)
            activity.finish()
        activityList.clear()
    }

}
