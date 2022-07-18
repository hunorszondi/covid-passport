package com.hunorszondi.covidpassport.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.hunorszondi.covidpassport.Session
import com.hunorszondi.covidpassport.api.ApiClient
import com.hunorszondi.covidpassport.api.ApiException
import com.hunorszondi.covidpassport.api.repository.ValidatorRepository
import com.hunorszondi.covidpassport.model.apiModels.VerifiedCertificateModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Handles the logic and the data management for the validator role
 */
class ValidatorViewModel : ViewModel() {
    //----------------------------------------API request utilities-----------------------------------
    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    private val repository : ValidatorRepository = ValidatorRepository(ApiClient.apiService)

    //----------------------------------Authenticator Main Fragment related---------------------------------

    var currentCertificate: VerifiedCertificateModel? = null

    //-------------------------------------------METHODS-----------------------------------------

    //----------------------------Authenticator Main Fragment related methods-------------------------------

    fun sendValidationResponse(response: Boolean, callback: (Boolean, String) -> Unit) {
        if (Session.instance.currentUser != null) {
            scope.launch {
                try {
                    val result = repository.sendValidationResponse(currentCertificate?.certificateId!!, response)
                    if (result?.data != null) {
                        callback(true, "Success")
                    } else {
                        callback(false, result?.error!!)
                    }
                } catch (error: ApiException) {
                    Log.d("ValidatorViewModel", error.serverError ?: "Unknown error with throw")
                    callback(false, error.serverError ?: "Unknown error with throw")
                }
            }
        }
    }

    fun getNextCertificate(callback: (Boolean, String) -> Unit) {
        if (Session.instance.currentUser != null) {
            scope.launch {
                try {
                    val result = repository.getNextCertificate()
                    if (result?.data != null) {
                        currentCertificate = result.data
                        callback(true, "Success")
                    } else {
                        currentCertificate = null
                        callback(false, result?.error!!)
                    }
                } catch (error: ApiException) {
                    Log.d("ValidatorViewModel", error.serverError ?: "Unknown error with throw")
                    callback(false, error.serverError ?: "Unknown error with throw")
                }
            }
        }
    }

}