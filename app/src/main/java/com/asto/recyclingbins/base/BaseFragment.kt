package com.asto.recyclingbins.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment<A : BaseActivity> : Fragment() {

    abstract val layoutId: Int
    lateinit var mView : View
    abstract val mActivity: A

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(layoutId, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(mView)
        bindinOnClickListener(mView)
        initDatas(mView)
    }

    abstract fun initViews(mView: View)

    abstract fun initDatas(mView : View)

    abstract fun bindinOnClickListener(mView : View)

    fun showLoading() {
        mActivity.showLoading()
    }

    fun dismissLoading() {
        mActivity.dismissLoading()
    }

    fun showToast(msg: String) {
        mActivity.showToast(msg)
    }

}