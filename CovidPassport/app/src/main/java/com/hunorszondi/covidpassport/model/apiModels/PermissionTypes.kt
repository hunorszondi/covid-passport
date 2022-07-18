package com.hunorszondi.covidpassport.model.apiModels

data class PermissionTypes (
    val ADMIN: String = "admin",
    val CITIZEN: String = "citizen",
    val AUTHENTICATOR: String = "authenticator",
    val VALIDATOR: String = "validator"
)