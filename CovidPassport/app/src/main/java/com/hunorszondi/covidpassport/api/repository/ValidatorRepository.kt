package com.hunorszondi.covidpassport.api.repository

import com.hunorszondi.covidpassport.api.ApiServiceInterface
import com.hunorszondi.covidpassport.model.apiModels.*


class ValidatorRepository(private val api : ApiServiceInterface) : BaseRepository() {

    suspend fun sendValidationResponse(certificateId: String, response: Boolean) : BaseResponse<String>?{
        val simpleRequestModel = SimpleRequestModel(response)
        return safeApiCall(
            call = { api.sendValidationResponse(certificateId, simpleRequestModel).await()},
            errorMessage = "Error verifying signature"
        )

    }

    suspend fun getNextCertificate() : BaseResponse<VerifiedCertificateModel>?{

        return safeApiCall(
            call = { api.getNextCertificate().await()},
            errorMessage = "Error verifying signature"
        )

    }
}