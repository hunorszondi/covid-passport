package com.hunorszondi.covidpassport.api.repository

import com.hunorszondi.covidpassport.api.ApiServiceInterface
import com.hunorszondi.covidpassport.model.apiModels.*
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.MultipartBody
import java.io.File


class CitizenRepository(private val api : ApiServiceInterface) : BaseRepository() {

    suspend fun getCertificates(userName: String) : BaseResponse<MutableList<VaccineCertificateModel>>?{

        return safeApiCall(
            call = { api.getCertificates(userName).await()},
            errorMessage = "Error Fetching Certificates"
        )

    }

    suspend fun upsertCertificate(type: String, requestId: String, certificate: VaccineCertificateRequestModel) : BaseResponse<VaccineCertificateModel>?{
        var idPhotoPart: MultipartBody.Part? = null
        if (certificate.idPhoto != null) {
            val photoFile = File(certificate.idPhoto)
            val fileReqBody = RequestBody.create(MediaType.parse("image/*"), photoFile)
            idPhotoPart = MultipartBody.Part.createFormData("idPhoto", photoFile.name, fileReqBody)
        }

        var docPhotoPart: MultipartBody.Part? = null
        if (certificate.docPhoto != null) {
            val photoFile = File(certificate.docPhoto)
            val fileReqBody = RequestBody.create(MediaType.parse("image/*"), photoFile)
            docPhotoPart = MultipartBody.Part.createFormData("docPhoto", photoFile.name, fileReqBody)
        }

        val dateTime = if (certificate.dateTime != null) {
            certificate.dateTime.toString()
        } else { "" }

        val age = if (certificate.age != null) {
            certificate.age.toString()
        } else { "" }

        val userNameReqBody = RequestBody.create(MediaType.parse("text/plain"), certificate.userName?:"")
        val docNameReqBody = RequestBody.create(MediaType.parse("text/plain"), certificate.docName?:"")
        val typeReqBody = RequestBody.create(MediaType.parse("text/plain"), certificate.type?:"")
        val idCardNumberBody = RequestBody.create(MediaType.parse("text/plain"), certificate.idCardNumber?:"")
        val dateTimeReqBody = RequestBody.create(MediaType.parse("text/plain"), dateTime)
        val ageReqBody = RequestBody.create(MediaType.parse("text/plain"), age)
        val centerNameReqBody = RequestBody.create(MediaType.parse("text/plain"), certificate.centerName?:"")
        val docSerialNrReqBody = RequestBody.create(MediaType.parse("text/plain"), certificate.docSerialNr?:"")

        if(type == "add") {
            return safeApiCall(
                call = { api.addCertificateToUser(requestId,
                    idPhotoPart,
                    docPhotoPart,
                    userNameReqBody,
                    docNameReqBody,
                    typeReqBody,
                    idCardNumberBody,
                    dateTimeReqBody,
                    ageReqBody,
                    centerNameReqBody,
                    docSerialNrReqBody
                ).await()},
                errorMessage = "Error adding Certificate"
            )
        }
        // If update
        return safeApiCall(
            call = { api.updateCertificate(requestId,
                idPhotoPart,
                docPhotoPart,
                userNameReqBody,
                docNameReqBody,
                typeReqBody,
                idCardNumberBody,
                dateTimeReqBody,
                ageReqBody,
                centerNameReqBody,
                docSerialNrReqBody
            ).await()},
            errorMessage = "Error updating Certificate"
        )
    }

    suspend fun removeCertificate(certificateId: String) : BaseResponse<String>?{

        return safeApiCall(
            call = { api.removeCertificate(certificateId).await()},
            errorMessage = "Error removing Certificate"
        )

    }
}