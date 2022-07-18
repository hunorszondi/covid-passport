package com.hunorszondi.covidpassport.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.hunorszondi.covidpassport.Session
import com.hunorszondi.covidpassport.api.ApiClient
import com.hunorszondi.covidpassport.api.ApiException
import com.hunorszondi.covidpassport.api.repository.AuthenticatorRepository
import com.hunorszondi.covidpassport.api.repository.CitizenRepository
import com.hunorszondi.covidpassport.model.apiModels.VerifiedCertificateModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Handles the logic and the data management for the authenticator role
 */
class AuthenticatorViewModel : ViewModel() {
    //----------------------------------------API request utilities-----------------------------------
    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    private val repository : AuthenticatorRepository = AuthenticatorRepository(ApiClient.apiService)

    //----------------------------------Authenticator Main Fragment related---------------------------------

    var currentVerifiedCertificate: VerifiedCertificateModel? = null

    //-------------------------------------------METHODS-----------------------------------------

    //----------------------------Authenticator Main Fragment related methods-------------------------------

    fun authenticateSignature(signature: String, callback: (Boolean, String) -> Unit) {
        if (Session.instance.currentUser != null) {
            scope.launch {
                try {
                    val result = repository.authenticateSignature(signature)
                    if (result?.data != null) {
                        currentVerifiedCertificate = result.data
                        callback(true, "Success")
                    } else {
                        callback(false, result?.error!!)
                    }
                } catch (error: ApiException) {
                    Log.d("AuthenticatorViewModel", error.serverError ?: "Unknown error with throw")
                    callback(false, error.serverError ?: "Unknown error with throw")
                }
            }
        }
    }

}