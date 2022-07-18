package com.hunorszondi.covidpassport

import com.hunorszondi.covidpassport.model.apiModels.UserModel
import android.content.Context
import com.google.gson.Gson
import android.content.SharedPreferences
import com.pusher.pushnotifications.PushNotifications

/**
 * Manages the user session
 */
class Session private constructor(context: Context){

    private val USER_ID: String = "currentUser"
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    var currentUser: UserModel? = null

    /**
     * Saves the newly logged in users data in memory and shared preferences
     */
    fun login(user: UserModel) {
        currentUser = user
        saveUserSession(user)
        PushNotifications.addDeviceInterest(user.userName)
    }

    /**
     * Deletes user data from memory and shared preferences
     */
    fun logout() {
        if(currentUser != null){
            PushNotifications.removeDeviceInterest(currentUser!!.userName)
        }
        currentUser = null
        removeUserSession()
    }

    /**
     * Returns if a user is logged in or not
     */
    fun isUserLoggedIn(): Boolean {
        return currentUser != null
    }

    /**
     * Returns if a user is logged in or not
     */
    fun hasMultiplePermissions(): Boolean {
        return currentUser?.permissions?.size ?: 0 > 1
    }

    /**
     * Loads the user information in sharedPreferences
     */
    fun loadUserSession() {
        val userJson: String? = sharedPreferences.getString(USER_ID, null)
        if(userJson != null) {
            currentUser = Gson().fromJson(userJson, UserModel::class.java)
        }
    }

    /**
     * Saves the user information in sharedPreferences
     */
    private fun saveUserSession(user: UserModel) {
        val userJson: String = Gson().toJson(user)
        sharedPreferences.edit()
            .putString(USER_ID, userJson)
            .apply()
    }

    /**
     * Removes all data from each shared preferences
     */
    private fun removeUserSession() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {

        lateinit var instance: Session
            private set

        /**
         * Initializing Session, in order to have access to shared preferences from anywhere in the app.
         * Call it only from App and only once!
         *
         * @param context Provide the application context
         */
        fun createInstance(context: Context) {
            instance = Session(context)
            instance.loadUserSession()
        }
    }
}
