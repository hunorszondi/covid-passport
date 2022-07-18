package com.hunorszondi.covidpassport.model.apiModels

import com.google.gson.annotations.Expose

data class VaccineCertificateRequestModel(
    @Expose
    var userName: String?,
    @Expose
    var docName: String?,
    @Expose
    var type: String?,
    @Expose
    var dateTime: Long?,
    @Expose
    var idCardNumber: String?,
    @Expose
    var age: Int?,
    @Expose
    var centerName: String?,
    @Expose
    var docSerialNr: String?,
    @Expose
    var idPhoto: String?,
    @Expose
    var docPhoto: String?
){
    constructor(): this(null, null, null, null, null, null, null, null, null, null)
}




