package com.hunorszondi.covidpassport.model.apiModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VaccineCertificateModel(
    @Expose
    @SerializedName("_id")
    val id: String,
    @Expose
    val userName: String,
    @Expose
    val docName: String,
    @Expose
    val type: String,
    @Expose
    val dateTime: Long,
    @Expose
    val idCardNumber: String,
    @Expose
    val age: Int,
    @Expose
    val centerName: String,
    @Expose
    val docSerialNr: String,
    @Expose
    val status: String,
    @Expose
    val signature: String?,
    @Expose
    val idPhoto: String?,
    @Expose
    val docPhoto: String?
)
