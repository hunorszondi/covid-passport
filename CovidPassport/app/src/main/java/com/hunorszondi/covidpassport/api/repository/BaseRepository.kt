package com.hunorszondi.covidpassport.api.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.hunorszondi.covidpassport.Session
import com.hunorszondi.covidpassport.api.ApiClient
import com.hunorszondi.covidpassport.api.ApiException
import com.hunorszondi.covidpassport.api.ApiResult
import com.hunorszondi.covidpassport.model.apiModels.BaseResponse
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException

open class BaseRepository{

    /**
     * Handles the response from the safeApiResult() function
     *
     * @param call function to call
     * @param errorMessage in case of error a general message linked to the called function
     *
     * @return positive or negative result
     */
    @Throws(ApiException::class)
    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>, errorMessage: String): T? {

        val result : ApiResult<T> = safeApiResult(call,errorMessage)
        var data : T? = null

        when(result) {
            is ApiResult.Success ->
                data = result.data
            is ApiResult.Error -> {
                Log.d("SafeApiCall", "$errorMessage " +
                        "& Exception - ${result.exception.serverError} " +
                        "& Code - ${result.exception.serverError}")
                throw result.exception
            }
        }


        return data

    }

    /**
     * Calls the api request in an error handled environment
     *
     * @param call function to call
     * @param errorMessage in case of error a general message linked to the called function
     *
     * @return result of the request ApiResult<T>
     */
    private suspend fun <T: Any> safeApiResult(call: suspend ()-> Response<T>, errorMessage: String) : ApiResult<T>{
        try {
            val response =  call.invoke()
            if(response.isSuccessful) {
                return ApiResult.Success(response.body()!!)
            }

            // Unauthorized access, probably outdated jwt token
            // try authentication with old credentials to receive a new jwt token
            if(response.code() == 401) {
                val result = UserRepository(ApiClient.apiService).authenticateUser(Session.instance.currentUser!!.userName,
                    Session.instance.currentUser!!.password)
                Session.instance.login(result!!.data)

                //recursion!!
                return safeApiResult(call, errorMessage)
            }

            // handle error
            val errorData: BaseResponse<*> = Gson().fromJson(response.errorBody()!!.string(), BaseResponse::class.java)

            val exception = ApiException(errorMessage, errorData.error, response.code())

            return ApiResult.Error(exception)
        } catch (error: SocketTimeoutException) {
            return ApiResult.Error(ApiException("SocketTimeoutException", "safeApiResult - SocketTimeoutException", 500))
        } catch (error: JsonSyntaxException) {
            return ApiResult.Error(ApiException("JsonSyntaxException", "safeApiResult - Unable to parse", 500))
        } catch (error: IllegalStateException) {
            return ApiResult.Error(ApiException("IllegalStateException", "404 - Not found", 404))
        } catch (error: Exception) {
            return ApiResult.Error(ApiException("UnknownException", "Some error occurred", 500))
        }
    }
}