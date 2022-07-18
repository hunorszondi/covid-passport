package com.hunorszondi.covidpassport.model.apiModels

import com.google.gson.annotations.Expose

data class AuthRequestModel (
    @Expose
    val userName: String,
    @Expose
    val password: String
)