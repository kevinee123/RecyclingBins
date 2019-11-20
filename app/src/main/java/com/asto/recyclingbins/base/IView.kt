package com.asto.recyclingbins.base

interface IView<P> {

    val presenter : P

    /**
     * 显示加载中
     */
    fun showLoading()

    /**
     * 隐藏加载
     */
    fun dismissLoading()

    /**
     * 显示Toast
     */
    fun showToast(msg: String)

    /**
     * 关闭Activity直到剩下几层
     *
     * @param remain 剩余Activity数
     */
    fun finishAndRemainHowActivity(remain: Int)

    /**
     * 关闭所有Activity
     */
    fun finishAllActivity()
}