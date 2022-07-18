package com.hunorszondi.covidpassport.api.repository

import com.hunorszondi.covidpassport.api.ApiServiceInterface
import com.hunorszondi.covidpassport.model.apiModels.*


class AdminRepository(private val api : ApiServiceInterface) : BaseRepository() {

    suspend fun updateUserPermissionsByUserName(userName: String, permissions: ArrayList<String>) : BaseResponse<String>?{
        val simpleRequestModel = SimpleRequestModel(permissions)
        return safeApiCall(
            call = { api.updateUserPermissionsById(userName, simpleRequestModel).await()},
            errorMessage = "Error fetching user by id failed"
        )
    }

    suspend fun getUserByUserName(userName: String) : BaseResponse<UserModel>?{

        return safeApiCall(
            call = { api.getUserByUserName(userName).await()},
            errorMessage = "Error fetching user by id failed"
        )
    }

    suspend fun deleteUserAccount(userName: String) : BaseResponse<String>?{

        return safeApiCall(
            call = { api.deleteUserAccount(userName).await()},
            errorMessage = "Error deleting user by id failed"
        )
    }
}