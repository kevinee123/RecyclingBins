package com.asto.autoweigh.core

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

var hash: Int = 0
var lastClickTime: Long = 0
var SPACE_TIME: Long = 1000
/**
 * 防止重复点击
 */
fun View.setOnClickDelayListener(clickAction: () -> Unit) {
    this.setOnClickListener {
        if (this.hashCode() != hash) {
            hash = this.hashCode()
            lastClickTime = System.currentTimeMillis()
            clickAction()
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > SPACE_TIME) {
                lastClickTime = System.currentTimeMillis()
                clickAction()
            }
        }
    }
}

/**
 * 创建布局
 */
fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = true): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

/**
 *
 */
fun EditText.getTextString(): String {
    return this.text.toString()
}

fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}
fun FragmentActivity.addFragment(fragment: Fragment, frameId: Int){
    supportFragmentManager.inTransaction { add(frameId, fragment) }
}


fun FragmentActivity.replaceFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction{replace(frameId, fragment)}
}

fun dp2px(context: Context, dpValue: Int) : Int{
    val scale = context.resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

//px转dp
fun px2dp(context: Context, pxValue: Int): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pxValue.toFloat(), context.resources.displayMetrics)
        .toInt()
}
