package com.hunorszondi.covidpassport.model.apiModels

import com.google.gson.annotations.Expose

data class BaseResponse<T> (
    @Expose
    val data: T,
    @Expose
    val error: String
)