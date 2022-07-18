package com.hunorszondi.covidpassport.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.hunorszondi.covidpassport.Session
import com.hunorszondi.covidpassport.api.ApiClient
import com.hunorszondi.covidpassport.api.ApiException
import com.hunorszondi.covidpassport.api.repository.UserRepository
import com.hunorszondi.covidpassport.model.apiModels.UserRegisterRequestModel
import com.hunorszondi.covidpassport.model.apiModels.PermissionTypes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Authentication logic and the data management
 */
class LoginViewModel : ViewModel() {
    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    private val repository: UserRepository = UserRepository(ApiClient.apiService)

    /**
     * Authenticates a user in the backend
     *
     * @param userName userName
     * @param password password
     * @param callback communicate back to the caller environment
     */
    fun login(userName: String, password: String, callback: (Boolean, String)->Unit) {
        scope.launch {
            try {
                val user = repository.authenticateUser(userName, password)
                if(user?.data != null) {
                    user.data.password = password
                    Session.instance.login(user.data)
                    callback(true, "Login successful")
                } else {
                    callback(false, user?.error?:"Unknown error no throw")
                }
            } catch (error: ApiException) {
                callback(false, error.serverError?:"Unknown error with throw")
            }
        }
    }

    /**
     * Registers a user in the backend
     *
     * @param userName userName
     * @param password password
     * @param firstName First name
     * @param lastName Last name
     * @param email email address
     * @param personalIdNumber Personal identification number
     * @param idCardNumber Identification card number
     * @param address Physical address
     * @param photo optional, a file path to an image
     * @param callback communicate back to the caller environment
     */
    fun register(userName: String,
                 password: String,
                 firstName: String,
                 lastName: String,
                 email: String,
                 personalIdNumber: String,
                 idCardNumber: String,
                 address: String,
                 photo: String?,
                 callback: (Boolean, String)->Unit) {

        if(userName.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()
            || email.isEmpty() || personalIdNumber.isEmpty() || idCardNumber.isEmpty() || address.isEmpty()) {
            callback(false, "Please fill each field to register")
            return
        }

        val registerModel = UserRegisterRequestModel(userName, firstName, lastName,
            password, email, personalIdNumber, idCardNumber, address, photo)

        scope.launch {
            try {
                val result = repository.registerUser(registerModel)
                if(result?.data != null) {
                    callback(true, result.data)
                } else {
                    callback(false, result?.error?:"Unknown error no throw")
                }
            } catch (error: ApiException) {
                Log.d("LoginViewModel", error.serverError?:"Unknown error with throw")
                callback(false, error.serverError?:"Unknown error with throw")
            }
        }
    }
}
