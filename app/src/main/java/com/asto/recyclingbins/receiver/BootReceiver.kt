package com.asto.recyclingbins.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.asto.recyclingbins.mvp.view.DemoActivity

/**
 * Created by nxk on 2019/11/14.
 * is use for: 监听开机广播
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == action_boot) {
            val mBootIntent = Intent(context, DemoActivity::class.java)
            // 下面这句话必须加上才能开机自动运行app的界面
            mBootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(mBootIntent)
        }
    }

    companion object {
        internal val action_boot = "android.intent.action.BOOT_COMPLETED"
    }
}
