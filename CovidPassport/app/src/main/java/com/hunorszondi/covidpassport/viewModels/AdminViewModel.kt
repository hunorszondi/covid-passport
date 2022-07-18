package com.hunorszondi.covidpassport.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.hunorszondi.covidpassport.Session
import com.hunorszondi.covidpassport.api.ApiClient
import com.hunorszondi.covidpassport.api.ApiException
import com.hunorszondi.covidpassport.api.repository.AdminRepository
import com.hunorszondi.covidpassport.api.repository.AuthenticatorRepository
import com.hunorszondi.covidpassport.model.apiModels.UserModel
import com.hunorszondi.covidpassport.model.apiModels.VerifiedCertificateModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Handles the logic and the data management for the admin role
 */
class AdminViewModel : ViewModel() {
    //----------------------------------------API request utilities-----------------------------------
    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    private val repository : AdminRepository = AdminRepository(ApiClient.apiService)

    //----------------------------------Authenticator Main Fragment related---------------------------------

    var foundUser: UserModel? = null

    //-------------------------------------------METHODS-----------------------------------------

    //----------------------------Authenticator Main Fragment related methods-------------------------------

    fun searchUser(userName: String, callback: (Boolean, String) -> Unit) {
        if (Session.instance.currentUser != null) {
            scope.launch {
                try {
                    val result = repository.getUserByUserName(userName)
                    if (result?.data != null) {
                        foundUser = result.data
                        callback(true, "Success")
                    } else {
                        callback(false, result?.error!!)
                    }
                } catch (error: ApiException) {
                    Log.d("AdminViewModel", error.serverError ?: "Unknown error with throw")
                    callback(false, error.serverError ?: "Unknown error with throw")
                }
            }
        }
    }

    fun updatePermissions(permissions: ArrayList<String>, callback: (Boolean, String) -> Unit) {
        if (Session.instance.currentUser != null) {
            scope.launch {
                try {
                    val result = repository.updateUserPermissionsByUserName(foundUser?.userName!!, permissions)
                    if (result?.data != null) {
                        callback(true, "Success")
                    } else {
                        callback(false, result?.error!!)
                    }
                } catch (error: ApiException) {
                    Log.d("AdminViewModel", error.serverError ?: "Unknown error with throw")
                    callback(false, error.serverError ?: "Unknown error with throw")
                }
            }
        }
    }

    fun deleteUserAccount(callback: (Boolean, String) -> Unit) {
        if (Session.instance.currentUser != null) {
            scope.launch {
                try {
                    val result = repository.deleteUserAccount(foundUser?.userName!!)
                    if (result?.data != null) {
                        callback(true, "Success")
                    } else {
                        callback(false, result?.error!!)
                    }
                } catch (error: ApiException) {
                    Log.d("AdminViewModel", error.serverError ?: "Unknown error with throw")
                    callback(false, error.serverError ?: "Unknown error with throw")
                }
            }
        }
    }

}