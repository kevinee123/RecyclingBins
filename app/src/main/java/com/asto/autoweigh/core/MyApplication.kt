package com.asto.autoweigh.core

import android.annotation.SuppressLint
import android.app.Application
import android.content.SharedPreferences
import com.asto.autoweigh.base.BaseActivity
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.umeng.commonsdk.UMConfigure

@SuppressLint("Registered")
class MyApplication : Application() {

    var activityList: ArrayList<BaseActivity> = ArrayList()
    private var token: String? = null
    private lateinit var sharedPreferences: SharedPreferences

    var isFirstLogin: Boolean
        get() = sharedPreferences.getBoolean("firstLogin", true)
        set(isFirstLogin) {
            val editor = sharedPreferences.edit()
            editor.putBoolean("firstLogin", isFirstLogin)
            editor.commit()
        }

    override fun onCreate() {
        super.onCreate()
        //初始化Logger
        Logger.addLogAdapter(AndroidLogAdapter())
        //初始化友盟
        UMConfigure.init(this, "5db8f6910cafb2744c000e6b", "mine", UMConfigure.DEVICE_TYPE_PHONE, null)
    }


    fun addActivity(activity: BaseActivity) {
        activityList.add(activity)
    }

    fun removeActivity(activity: BaseActivity) {
        activityList.remove(activity)
    }

    fun setToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.commit()
        this.token = token
    }

    fun getToken(): String? {
        token = sharedPreferences.getString("token", null)
        return token
    }


    fun getActivityList(): MutableList<BaseActivity> {
        return activityList
    }

}
