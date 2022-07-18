package com.hunorszondi.covidpassport.model.apiModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserModel(
    @Expose
    @SerializedName("_id")
    val userId: String,
    @Expose
    val userName: String,
    @Expose
    val firstName: String,
    @Expose
    val lastName: String,
    @Expose
    var password: String,
    @Expose
    var permissions: Array<String>,
    @Expose
    val email: String,
    @Expose
    val personalIdNumber: String?,
    @Expose
    val idCardNumber: String?,
    @Expose
    val address: String?,
    @Expose
    val photo: String?,
    @Expose
    val vaccineCertificates: ArrayList<VaccineCertificateModel>,
    @Expose
    var token: String

)




