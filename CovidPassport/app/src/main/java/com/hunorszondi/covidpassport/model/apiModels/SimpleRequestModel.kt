package com.hunorszondi.covidpassport.model.apiModels

import com.google.gson.annotations.Expose

data class SimpleRequestModel<T> (
    @Expose
    val data: T
)