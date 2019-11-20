package com.asto.recyclingbins.base

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.android.tu.loadingdialog.LoadingDialog
import com.asto.recyclingbins.R
import com.asto.recyclingbins.core.Common
import com.asto.recyclingbins.core.MyApplication
import com.asto.recyclingbins.listener.OnSerialPortDataListener
import com.asto.recyclingbins.listener.OnSerialPortListener
import com.asto.recyclingbins.util.SerialPortUtils
import com.baidu.tts.client.SpeechError
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.SpeechSynthesizerListener
import com.baidu.tts.client.TtsMode
import com.umeng.analytics.MobclickAgent


abstract class BaseActivity : FragmentActivity() {
    companion object {
        var mSpeechSynthesizer: SpeechSynthesizer? = null//语音

        lateinit var mSerialPortUtils: SerialPortUtils//串口工具类
    }

    var mLoadingDialog: LoadingDialog? = null
    val application: MyApplication
        get() {
            return getApplication() as MyApplication
        }
    var mOnSerialPortDataListener: OnSerialPortDataListener? = null

    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)

        application.addActivity(this)

        if (mLoadingDialog == null)
            initLoadingDialog()
        initSerialPort()//初始化串口通信
        if (mSpeechSynthesizer == null) initBaiduVoice()//百度语音合成初始化

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
        mSpeechSynthesizer?.auth(TtsMode.MIX)// 离在线混合
//        mSpeechSynthesizer.auth(TtsMode.ONLINE)  // 纯在线
        //初始化合成引擎
        mSpeechSynthesizer?.initTts(TtsMode.MIX)
        setMaxVolume()//设置最大音量
    }

    /**
     * 设置最大音量
     */
    private fun setMaxVolume(){
        val audioMa = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioMa.setStreamVolume(AudioManager.STREAM_MUSIC,audioMa.getStreamMaxVolume
            (AudioManager.STREAM_MUSIC),AudioManager.FLAG_SHOW_UI)
    }

    /**
     * 初始化串口通信
     */
    private fun initSerialPort() {
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
                            showToast(getString(R.string.doSuccess))
                            if (mOnSerialPortDataListener != null) {
                                val data = text.substring(
                                    text.length - 4 - (text.substring(
                                        2,
                                        4
                                    ).toInt() - 6) * 2, text.length - 4
                                )
                                mOnSerialPortDataListener?.onSuccess(text.substring(8, 10), data)
                            }
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
        val activityList = application.getActivityList()
        for (activity in activityList)
            activity.finish()
        activityList.clear()
    }

}
