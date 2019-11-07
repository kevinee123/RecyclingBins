package com.asto.autoweigh.base

import com.asto.autoweigh.model.ApiModel
import com.asto.autoweigh.util.RetrofitUtil

interface IPresenter<V>{
    val model: ApiModel
        get() = RetrofitUtil.getRetrofit().create(ApiModel::class.java)
    val view : V

}