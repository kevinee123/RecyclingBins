package com.asto.recyclingbins.base

import android.Manifest.permission.*
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.android.tu.loadingdialog.LoadingDialog
import com.asto.recyclingbins.R
import com.asto.recyclingbins.bean.EventMessage
import com.asto.recyclingbins.bean.RecyclingTypeBean
import com.asto.recyclingbins.bean.SerialPortBean
import com.asto.recyclingbins.core.Common
import com.asto.recyclingbins.core.MyApplication
import com.asto.recyclingbins.listener.OnSerialPortListener
import com.asto.recyclingbins.mvp.view.SettlementListActivity
import com.asto.recyclingbins.util.SerialPortUtils
import com.baidu.tts.chainofresponsibility.logger.LoggerProxy
import com.baidu.tts.client.SpeechError
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.SpeechSynthesizerListener
import com.baidu.tts.client.TtsMode
import com.jiangdg.usbcamera.UVCCameraHelper
import com.umeng.analytics.MobclickAgent
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity


abstract class BaseActivity : FragmentActivity() {
    companion object {
        var mSpeechSynthesizer: SpeechSynthesizer? = null //语音
        var isLogin = false //是否登录
        var list : List<RecyclingTypeBean>? = null
        var mSerialPortUtils: SerialPortUtils? = null //串口工具类
        var isRequest = false //是否已连接USB
    }

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

        if (mLoadingDialog == null) initLoadingDialog()
        initSerialPort() //初始化串口通信
        initPermission() //权限申请
        if (mSpeechSynthesizer == null) initBaiduVoice() //百度语音合成初始化

        initData()
        bindinOnClickListener()
    }

    /**
     * 百度语音合成
     */
    private fun initBaiduVoice() {
        //获取 SpeechSynthesizer 实例
        mSpeechSynthesizer = SpeechSynthesizer.getInstance()
        //设置当前的Context
        mSpeechSynthesizer?.setContext(this)
        //语音合成成功回调
        mSpeechSynthesizer?.setSpeechSynthesizerListener(object : SpeechSynthesizerListener {
            override fun onSynthesizeStart(p0: String?) {
            }

            override fun onSpeechFinish(p0: String?) {
            }

            override fun onSpeechProgressChanged(p0: String?, p1: Int) {
            }

            override fun onSynthesizeFinish(p0: String?) {
            }

            override fun onSpeechStart(p0: String?) {
            }

            override fun onSynthesizeDataArrived(p0: String?, p1: ByteArray?, p2: Int) {
            }

            override fun onError(p0: String?, p1: SpeechError?) {
            }

        })
        //设置 App Id和 App Key 及 App Secret
        mSpeechSynthesizer?.setAppId(Common.BD_APP_ID)
        mSpeechSynthesizer?.setApiKey(Common.BD_APP_KEY, Common.BD_APP_SECRET)
        //授权检验接口
        mSpeechSynthesizer?.auth(TtsMode.MIX) // 离在线混合
        //        mSpeechSynthesizer.auth(TtsMode.ONLINE)  // 纯在线
        //初始化合成引擎
        mSpeechSynthesizer?.initTts(TtsMode.MIX)
        setMaxVolume() //设置最大音量
        LoggerProxy.printable(true) // 日志打印在logcat中
    }

    /**
     * 设置最大音量
     */
    private fun setMaxVolume() {
        val audioMa = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioMa.setStreamVolume(
            AudioManager.STREAM_MUSIC, audioMa.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_SHOW_UI
        )
        mSpeechSynthesizer?.setStereoVolume(1.0f, 1.0f)
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private fun initPermission() {
        val permissions = arrayOf(INTERNET, ACCESS_NETWORK_STATE, MODIFY_AUDIO_SETTINGS, WRITE_EXTERNAL_STORAGE, WRITE_SETTINGS, READ_PHONE_STATE, ACCESS_WIFI_STATE, CHANGE_WIFI_STATE)

        val toApplyList = ArrayList<String>()

        for (perm in permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                //进入到这里代表没有权限.
                toApplyList.add(perm)
            }
        }
        val tmpList = arrayOfNulls<String>(toApplyList.size)
        if (toApplyList.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123)
        }

    }


    /**
     * 初始化串口通信
     */
    private fun initSerialPort() {
        if (mSerialPortUtils != null)
            return
        //监听数据返回
        mSerialPortUtils = SerialPortUtils.get(this, object : OnSerialPortListener {
            override fun onSend(text: String) {
                //发送的完整数据
            }

            override fun onResult(text: String) {
                //接收的完整数据
                if (text.length > 10) {
                    when (text.substring(text.length - 10, text.length - 8).toInt()) {
                        //执行成功
                        0 -> {
//                            showToast(getString(R.string.doSuccess))
                            val data = text.substring(
                                text.length - 4 - (text.substring(
                                    2, 4
                                ).toInt() - 6) * 2, text.length - 4
                            )
//                            val bean = SerialPortBean(text.substring(8, 10), data)
                            EventBus.getDefault().post(EventMessage(text.substring(8, 10),data))
                        }
                        //投递门有异物
                        2 -> {
                            showToast(getString(R.string.door_have_foreign_matter))
                        }
                        //开垃圾投递门电机超时
                        3 -> {
                            showToast(getString(R.string.open_delivery_door_overtime))
                        }
                        //关垃圾投递门电机超时
                        4 -> {
                            showToast(getString(R.string.close_delivery_door_overtime))
                        }
                        //投递门电机堵转
                        5 -> {
                            showToast(getString(R.string.delivery_door_locked_rotor))
                        }
                        //eeprom没有初始化
                        9 -> {
                            showToast(getString(R.string.eeprom_no_init))
                        }
                        //连续读数变化超过5g
                        11 -> {
                            showToast(getString(R.string.weight_instability))
                        }
                        //命令类型错误
                        12 -> {
                            showToast(getString(R.string.command_type_error))
                        }
                    }
                }

            }
        })
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
        val loadBuilder = LoadingDialog.Builder(this).setMessage("加载中...").setCancelable(true)
            .setCancelOutside(true)
        mLoadingDialog = loadBuilder.create()
    }

    fun showLoading() {
        if (mLoadingDialog == null) initLoadingDialog()
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


    fun showToast(id: Int) {
        val toast = Toast.makeText(this, resources.getString(id), Toast.LENGTH_SHORT)
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
        var activityList = application.getActivityList()
        for (activity in activityList) activity.finish()
        activityList.clear()
    }

    override fun finish() {
        super.finish()
        val activityList = application.getActivityList()
        activityList.removeAt(activityList.size - 1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
        if (mSpeechSynthesizer == null) initBaiduVoice() //百度语音合成初始化
    }

    fun loginOut(isFinalFinish : Boolean = false){
        isLogin = false
        var isAdded = false
        list?.forEach {
            if (it.weight >0) isAdded = true
        }
        if(!isFinalFinish && isAdded){
            startActivity<SettlementListActivity>()
        }else{
            list = null
            EventBus.getDefault().post(EventMessage<String>(Common.EB_CODE_MAIN_FINISH))
            finishAndRemainHowActivity(1)
        }
    }

}
