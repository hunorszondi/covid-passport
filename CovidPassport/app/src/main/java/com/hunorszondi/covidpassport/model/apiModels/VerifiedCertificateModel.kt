package com.hunorszondi.covidpassport.model.apiModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hunorszondi.covidpassport.utils.DateUtil

data class VerifiedCertificateModel(
    @Expose
    val certificateId: String,
    @Expose
    val firstName: String,
    @Expose
    val lastName: String,
    @Expose
    val email: String,
    @Expose
    val personalIdNumber: String,
    @Expose
    val idCardNumber: String,
    @Expose
    val address: String,
    @Expose
    val photo: String?,
    @Expose
    val type: String,
    @Expose
    val dateTime: Long,
    @Expose
    val age: Int,
    @Expose
    val centerName: String,
    @Expose
    val docSerialNr: String,
    @Expose
    val idPhoto: String?,
    @Expose
    val docPhoto: String?,
    @Expose
    val validatorUserName: String?,
    @Expose
    val validationDateTime: Long?,
    @Expose
    val blockChainValidation: String?
) {
    override fun toString(): String {
        val sdf = DateUtil.getDateTimeFormat()
        val dateTimeFormatted = sdf.format(dateTime)
        var baseString = "First Name:  $firstName \n\n" +
                "Last Name:  $lastName \n\n" +
                "Email:  $email \n\n" +
                "Personal ID Number:  $personalIdNumber \n\n" +
                "ID Card Number:  $idCardNumber \n\n" +
                "Address:  $address \n\n" +
                "Type:  $type \n\n" +
                "Date of certificate:  $dateTimeFormatted \n\n" +
                "Age:  $age \n\n" +
                "Center Name:  $centerName \n\n" +
                "Doc Serial Number:  $docSerialNr \n\n"

        if (validatorUserName != null) {
            baseString += "BlockChain validation:  $blockChainValidation \n\n"
        }

        if (validatorUserName != null) {
            baseString += "Validator UserName:  $validatorUserName \n\n"
        }
        if (validationDateTime != null) {
            val validationDateTimeFormatted = sdf.format(dateTime)
            baseString += "Validation Date:  $validationDateTimeFormatted \n\n"
        }
        return baseString
    }
}
