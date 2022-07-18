package com.hunorszondi.covidpassport.api.repository

import com.hunorszondi.covidpassport.api.ApiServiceInterface
import com.hunorszondi.covidpassport.model.apiModels.*


class AuthenticatorRepository(private val api : ApiServiceInterface) : BaseRepository() {

    suspend fun authenticateSignature(signature: String) : BaseResponse<VerifiedCertificateModel>?{

        return safeApiCall(
            call = { api.authenticateSignature(signature).await()},
            errorMessage = "Error verifying signature"
        )

    }
}