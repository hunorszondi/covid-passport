package com.hunorszondi.covidpassport.model.apiModels

import com.google.gson.annotations.Expose

data class UserUpdateRequestModel(
    @Expose
    var firstName: String?,
    @Expose
    var lastName: String?,
    @Expose
    var password: String?,
    @Expose
    var permissions: Array<String>?,
    @Expose
    var email: String?,
    @Expose
    var personalIdNumber: String?,
    @Expose
    var idCardNumber: String?,
    @Expose
    var address: String?,
    @Expose
    var photo: String?
) {
    constructor(): this(null, null, null, null, null, null, null, null, null)
}




