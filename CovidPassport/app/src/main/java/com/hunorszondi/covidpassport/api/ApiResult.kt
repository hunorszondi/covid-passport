package com.hunorszondi.covidpassport.api

/**
 * Api result data container
 */
sealed class ApiResult<out T: Any> {
    data class Success<out T : Any>(val data: T) : ApiResult<T>()
    data class Error(val exception: ApiException) : ApiResult<Nothing>()
}