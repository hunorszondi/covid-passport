package com.hunorszondi.covidpassport.model.apiModels

import com.google.gson.annotations.Expose

data class UserRegisterRequestModel(
    @Expose
    val userName: String,
    @Expose
    var firstName: String?,
    @Expose
    var lastName: String?,
    @Expose
    var password: String?,
    @Expose
    var email: String?,
    @Expose
    var personalIdNumber: String?,
    @Expose
    var idCardNumber: String?,
    @Expose
    var address: String?,
    @Expose
    val file: String?
)




