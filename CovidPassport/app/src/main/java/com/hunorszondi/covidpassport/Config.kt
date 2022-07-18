package com.hunorszondi.covidpassport

/**
 * Stores all the necessary configurations for the app
 */
data class Config(
    val apiBaseUrl: String = "https://covid-passport-huni.herokuapp.com/",
//    val apiBaseUrl: String = "https://039edd8607d3.ngrok.io",
    val pusherInstance: String = "79da0046-c72a-47f2-bf19-fc58d34d1047"
)