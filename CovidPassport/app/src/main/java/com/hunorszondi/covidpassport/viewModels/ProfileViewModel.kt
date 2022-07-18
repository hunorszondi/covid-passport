package com.hunorszondi.covidpassport.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.hunorszondi.covidpassport.Session
import com.hunorszondi.covidpassport.api.ApiClient
import com.hunorszondi.covidpassport.api.ApiException
import com.hunorszondi.covidpassport.api.repository.UserRepository
import com.hunorszondi.covidpassport.model.apiModels.UserUpdateRequestModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Handles the logic and data management for the profile view
 */
class ProfileViewModel : ViewModel() {
    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    private val repository: UserRepository = UserRepository(ApiClient.apiService)

    /**
     * Updates user information in the backend
     *
     * @param password Password
     * @param firstName First name
     * @param lastName Last name
     * @param email email address
     * @param personalIdNumber Personal identification number
     * @param idCardNumber Identification card number
     * @param address Physical address
     * @param photo optional, a file path to an image
     * @param callback communicate back to the caller environment
     */
    fun updateProfile(password: String,
                 firstName: String,
                 lastName: String,
                 email: String,
                 personalIdNumber: String,
                 idCardNumber: String,
                 address: String,
                 photo: String?,
                 callback: (Boolean, String)->Unit) {

        val user = Session.instance.currentUser!!

        if(password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
            || personalIdNumber.isEmpty() || idCardNumber.isEmpty() || address.isEmpty()) {
            callback(false, "Please fill every field")
            return
        }

        val updateModel = UserUpdateRequestModel()

        if(password != user.password) {
            updateModel.password = password
        }

        if(firstName != user.firstName) {
            updateModel.firstName = firstName
        }

        if(lastName != user.lastName) {
            updateModel.lastName = lastName
        }

        if(email != user.email) {
            updateModel.email = email
        }

        if(personalIdNumber != user.personalIdNumber) {
            updateModel.personalIdNumber = personalIdNumber
        }

        if(idCardNumber != user.idCardNumber) {
            updateModel.idCardNumber = idCardNumber
        }

        if(address != user.address) {
            updateModel.address = address
        }

        if(photo != null) {
            updateModel.photo = photo
        }

        scope.launch {
            try {
                val result = repository.updateUser(user.userId, updateModel)
                if(result?.data != null) {
                    result.data.password = password
                    result.data.token = Session.instance.currentUser!!.token
                    Session.instance.login(result.data)
                    callback(true, "User successfully updated")
                } else {
                    callback(false, result?.error?:"Unknown error no throw")
                }
            } catch (error: ApiException) {
                Log.d("ProfileViewModel", error.serverError?:"Unknown error with throw")
                callback(false, error.serverError?:"Unknown error with throw")
            }
        }
    }

    /**
     * Deletes the active user account
     *
     * @param callback communicate back to the caller environment
     */
    fun deleteProfile(callback: (Boolean, String)->Unit) {
        if(Session.instance.currentUser != null) {
            scope.launch {
                try {
                    val result = repository.deleteUserAccount()
                    if(result?.data != null) {
                        callback(true, result.data)
                    } else {
                        callback(false, result?.error?:"Unknown error no throw")
                    }
                } catch (error: ApiException) {
                    Log.d("ProfileViewModel", error.serverError?:"Unknown error with throw")
                    callback(false, error.serverError?:"Unknown error with throw")
                }
            }
        }
    }
}