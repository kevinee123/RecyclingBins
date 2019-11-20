package com.asto.recyclingbins.base

import com.asto.recyclingbins.model.ApiModel
import com.asto.recyclingbins.util.RetrofitUtil

interface IPresenter<V>{
    val model: ApiModel
        get() = RetrofitUtil.getRetrofit().create(ApiModel::class.java)
    val view : V

}