package com.hunorszondi.covidpassport.api

import com.hunorszondi.covidpassport.Config
import com.hunorszondi.covidpassport.Session
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient{

    /**
     * Creating Auth Interceptor to add api_key query in front of all the requests.
     */
    private val authInterceptor = Interceptor {chain->
        val newUrl = chain.request().url()
            .newBuilder()
            .build()

        val requestBuilder = chain.request()
            .newBuilder()
            .addHeader("Accept", "*/*")
            .url(newUrl)

        if(Session.instance.isUserLoggedIn()) {
            requestBuilder.addHeader("Authorization", "Bearer ${Session.instance.currentUser?.token}")
        }

        val newRequest = requestBuilder.build()

        chain.proceed(newRequest)

    }

    /**
     * Creating logger
     */
    private val logging: HttpLoggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)


    /**
     * OkhttpClient for building http request url
     */
    private val apiClient = OkHttpClient().newBuilder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()


    /**
     * Building retrofit instance
     */
    private fun retrofit() : Retrofit = Retrofit.Builder()
        .client(apiClient)
        .baseUrl(Config().apiBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()


    val apiService : ApiServiceInterface = retrofit().create(ApiServiceInterface::class.java)
}