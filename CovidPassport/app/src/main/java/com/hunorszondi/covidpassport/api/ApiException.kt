package com.hunorszondi.covidpassport.api

import java.lang.Exception

/**
 * Custom exception for api responses
 */
class ApiException(message: String?,
                   val serverError: String?,
                   val code: Int): Exception(message)