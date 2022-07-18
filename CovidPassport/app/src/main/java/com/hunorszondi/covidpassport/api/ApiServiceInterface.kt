package com.hunorszondi.covidpassport.api

import com.google.gson.annotations.Expose
import com.hunorszondi.covidpassport.model.apiModels.*
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


/**
 * Collection of backend api endpoints
 */
interface ApiServiceInterface {
    //------------------------------------- User Requests ------------------------------------------
    @POST("/users/authenticate")
    fun authenticateUser(@Body auth: AuthRequestModel): Deferred<Response<BaseResponse<UserModel>>>

    @Multipart
    @POST("/users/register")
    fun registerUserWithImage(@Part file: MultipartBody.Part,
                              @Part("userName") userName: RequestBody,
                              @Part("firstName") firstName: RequestBody,
                              @Part("lastName") lastName: RequestBody,
                              @Part("personalIdNumber") personalIdNumber: RequestBody,
                              @Part("idCardNumber") idCardNumber: RequestBody,
                              @Part("address") address: RequestBody,
                              @Part("password") password: RequestBody,
                              @Part("email") email: RequestBody): Deferred<Response<BaseResponse<String>>>

    @POST("/users/register")
    fun registerUserWithoutImage(@Body userRegisterModel: UserRegisterRequestModel): Deferred<Response<BaseResponse<String>>>

    @Multipart
    @PUT("/users/id/{userid}")
    fun updateUserWithImage(@Path("userid") userId: String,
                            @Part file: MultipartBody.Part,
                            @Part("firstName") firstName: RequestBody,
                            @Part("lastName") lastName: RequestBody,
                            @Part("personalIdNumber") personalIdNumber: RequestBody,
                            @Part("idCardNumber") idCardNumber: RequestBody,
                            @Part("address") address: RequestBody,
                            @Part("password") password: RequestBody,
                            @Part("email") email: RequestBody
    ): Deferred<Response<BaseResponse<UserModel>>>

    @PUT("/users/id/{userid}")
    fun updateUserWithoutImage(@Path("userid") userId: String,
                               @Body userUpdateBody: UserUpdateRequestModel
    ): Deferred<Response<BaseResponse<UserModel>>>

    @DELETE("/users")
    fun deleteSelfUserAccount(): Deferred<Response<BaseResponse<String>>>

    //------------------------------------- Citizen Certificate Requests ------------------------------------------

    @GET("/citizen/certificates/{username}")
    fun getCertificates(@Path("username") userName: String): Deferred<Response<BaseResponse<MutableList<VaccineCertificateModel>>>>

    @Multipart
    @POST("/citizen/certificates/{username}")
    fun addCertificateToUser(@Path("username") username: String,
                                     @Part idPhoto: MultipartBody.Part?,
                                     @Part docPhoto: MultipartBody.Part?,
                                     @Part("userName") userName: RequestBody,
                                     @Part("docName") docName: RequestBody,
                                     @Part("type") type: RequestBody,
                                     @Part("idCardNumber") idCardNumber: RequestBody,
                                     @Part("dateTime") dateTime: RequestBody,
                                     @Part("age") age: RequestBody,
                                     @Part("centerName") centerName: RequestBody,
                                     @Part("docSerialNr") docSerialNr: RequestBody): Deferred<Response<BaseResponse<VaccineCertificateModel>>>

    @Multipart
    @PUT("/citizen/certificates/{certificateId}")
    fun updateCertificate(@Path("certificateId") certificateId: String,
                                     @Part idPhoto: MultipartBody.Part?,
                                     @Part docPhoto: MultipartBody.Part?,
                                     @Part("userName") userName: RequestBody,
                                     @Part("docName") docName: RequestBody,
                                     @Part("type") type: RequestBody,
                                     @Part("idCardNumber") idCardNumber: RequestBody,
                                     @Part("dateTime") dateTime: RequestBody,
                                     @Part("age") age: RequestBody,
                                     @Part("centerName") centerName: RequestBody,
                                     @Part("docSerialNr") docSerialNr: RequestBody): Deferred<Response<BaseResponse<VaccineCertificateModel>>>

    @DELETE("/citizen/certificates/{certificateId}")
    fun removeCertificate(@Path("certificateId") certificateId: String): Deferred<Response<BaseResponse<String>>>


    //------------------------------------- Authenticator Requests ------------------------------------------
    @GET("/authenticator/verifysignature/{signature}")
    fun authenticateSignature(@Path("signature") signature: String): Deferred<Response<BaseResponse<VerifiedCertificateModel>>>

    //------------------------------------- Validator Requests ------------------------------------------
    @POST("/validator/validate/{certificateId}")
    fun sendValidationResponse(@Path("certificateId") certificateId: String,
                               @Body response: SimpleRequestModel<Boolean>): Deferred<Response<BaseResponse<String>>>

    @GET("/validator/nextcertificate")
    fun getNextCertificate(): Deferred<Response<BaseResponse<VerifiedCertificateModel>>>

    //------------------------------------- Admin Requests ------------------------------------------

    @GET("/admin/users/{username}")
    fun getUserByUserName(@Path("username") userName: String): Deferred<Response<BaseResponse<UserModel>>>

    @PUT("/admin/users/{username}/permissions")
    fun updateUserPermissionsById(@Path("username") userName: String,
                               @Body permissions: SimpleRequestModel<ArrayList<String>>
    ): Deferred<Response<BaseResponse<String>>>

    @DELETE("/admin/users/{userName}")
    fun deleteUserAccount(@Path("userName") userName: String): Deferred<Response<BaseResponse<String>>>
}