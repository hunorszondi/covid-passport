package com.hunorszondi.covidpassport.api.repository

import com.hunorszondi.covidpassport.api.ApiServiceInterface
import com.hunorszondi.covidpassport.model.apiModels.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class UserRepository(private val api : ApiServiceInterface): BaseRepository() {

    suspend fun authenticateUser(userName: String, password: String) : BaseResponse<UserModel>?{

        val authBody = AuthRequestModel(userName, password)

        return safeApiCall(
            call = { api.authenticateUser(authBody).await()},
            errorMessage = "Error authentication failed"
        )
    }

    suspend fun registerUser(regModelUser: UserRegisterRequestModel) : BaseResponse<String>?{

        val userResponse: BaseResponse<String>?

        if(regModelUser.file != null) {
            val photo = File(regModelUser.file)
            val fileReqBody = RequestBody.create(MediaType.parse("image/*"), photo)
            val imagePart = MultipartBody.Part.createFormData("file", photo.name, fileReqBody)

            val userNameReqBody = RequestBody.create(MediaType.parse("text/plain"), regModelUser.userName)
            val firstNameReqBody = RequestBody.create(MediaType.parse("text/plain"), regModelUser.firstName?:"")
            val lastNameReqBody = RequestBody.create(MediaType.parse("text/plain"), regModelUser.lastName?:"")
            val personalIdNumberReqBody = RequestBody.create(MediaType.parse("text/plain"), regModelUser.personalIdNumber?:"")
            val idCardNumberReqBody = RequestBody.create(MediaType.parse("text/plain"), regModelUser.idCardNumber?:"")
            val addressReqBody = RequestBody.create(MediaType.parse("text/plain"), regModelUser.address?:"")
            val passwordReqBody = RequestBody.create(MediaType.parse("text/plain"), regModelUser.password?:"")
            val emailReqBody = RequestBody.create(MediaType.parse("text/plain"), regModelUser.email?:"")

            userResponse = safeApiCall(
                call = { api.registerUserWithImage(imagePart,
                    userNameReqBody,
                    firstNameReqBody,
                    lastNameReqBody,
                    personalIdNumberReqBody,
                    idCardNumberReqBody,
                    addressReqBody,
                    passwordReqBody,
                    emailReqBody).await() },
                errorMessage = "Error registration with image failed"
            )
        } else {
            userResponse = safeApiCall(
                call = { api.registerUserWithoutImage(regModelUser).await() },
                errorMessage = "Error registration without image failed"
            )
        }

        return userResponse
    }

    suspend fun updateUser(userId: String, userUpdateModel: UserUpdateRequestModel) : BaseResponse<UserModel>?{
        val userResponse: BaseResponse<UserModel>?

        if(userUpdateModel.photo != null) {
            val photo = File(userUpdateModel.photo)
            val fileReqBody = RequestBody.create(MediaType.parse("image/*"), photo)
            val imagePart = MultipartBody.Part.createFormData("file", photo.name, fileReqBody)

            val firstNameReqBody = RequestBody.create(MediaType.parse("text/plain"), userUpdateModel.firstName?:"")
            val lastNameReqBody = RequestBody.create(MediaType.parse("text/plain"), userUpdateModel.lastName?:"")
            val personalIdNumberReqBody = RequestBody.create(MediaType.parse("text/plain"), userUpdateModel.personalIdNumber?:"")
            val idCardNumberReqBody = RequestBody.create(MediaType.parse("text/plain"), userUpdateModel.idCardNumber?:"")
            val addressReqBody = RequestBody.create(MediaType.parse("text/plain"), userUpdateModel.address?:"")
            val passwordReqBody = RequestBody.create(MediaType.parse("text/plain"), userUpdateModel.password?:"")
            val emailReqBody = RequestBody.create(MediaType.parse("text/plain"), userUpdateModel.email?:"")

            userResponse = safeApiCall(
                call = { api.updateUserWithImage(userId,
                    imagePart,
                    firstNameReqBody,
                    lastNameReqBody,
                    personalIdNumberReqBody,
                    idCardNumberReqBody,
                    addressReqBody,
                    passwordReqBody,
                    emailReqBody).await() },
                errorMessage = "Error profile update with image failed"
            )
        } else {
            userResponse = safeApiCall(
                call = { api.updateUserWithoutImage(userId, userUpdateModel).await() },
                errorMessage = "Error profile update without image failed"
            )
        }

        return userResponse
    }

    suspend fun deleteUserAccount() : BaseResponse<String>?{

        return safeApiCall(
            call = { api.deleteSelfUserAccount().await()},
            errorMessage = "Error deleting user by id failed"
        )
    }
}