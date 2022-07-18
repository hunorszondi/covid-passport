package com.hunorszondi.covidpassport.ui.splash


import android.content.Intent
import android.os.Bundle
import android.os.Handler

import java.util.concurrent.TimeUnit

import androidx.appcompat.app.AppCompatActivity
import com.hunorszondi.covidpassport.R
import com.hunorszondi.covidpassport.Session
import com.hunorszondi.covidpassport.model.apiModels.PermissionTypes
import com.hunorszondi.covidpassport.ui.admin.AdminActivity
import com.hunorszondi.covidpassport.ui.auth.AuthActivity
import com.hunorszondi.covidpassport.ui.authenticator.AuthenticatorActivity
import com.hunorszondi.covidpassport.ui.citizen.CitizenActivity
import com.hunorszondi.covidpassport.ui.common.AccessSelector
import com.hunorszondi.covidpassport.ui.validator.ValidatorActivity

/**
 * First screen appearing when the app starts.
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            checkUserSession()
        }, TimeUnit.SECONDS.toMillis(1))

    }

    /**
     * Decides which screen to show depending on the stored user session
     */
    private fun checkUserSession() {
        if(Session.instance.isUserLoggedIn()) {
            if (Session.instance.hasMultiplePermissions()) {
                AccessSelector(this, this.layoutInflater, {
                    when(it) {
                        PermissionTypes().CITIZEN -> openCitizen()
                        PermissionTypes().AUTHENTICATOR -> openAuthenticator()
                        PermissionTypes().VALIDATOR -> openValidator()
                        PermissionTypes().ADMIN -> openAdmin()
                    }
                }, {
                    finish()
                }).show()
            } else {
                when(Session.instance.currentUser?.permissions?.get(0)) {
                    PermissionTypes().CITIZEN -> openCitizen()
                    PermissionTypes().AUTHENTICATOR -> openAuthenticator()
                    PermissionTypes().VALIDATOR -> openValidator()
                    PermissionTypes().ADMIN -> openAdmin()
                }
            }
        } else {
            openLogin()
        }
    }

    /**
     * Opens AuthActivity -> LoginFragment
     */
    private fun openLogin() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    /**
     * Opens CitizenActivity -> CertificateManagerFragment
     */
    private fun openCitizen() {
        startActivity(Intent(this, CitizenActivity::class.java))
        finish()
    }

    /**
     * Opens AuthenticatorActivity
     */
    private fun openAuthenticator() {
        startActivity(Intent(this, AuthenticatorActivity::class.java))
        finish()
    }

    /**
     * Opens ValidatorActivity
     */
    private fun openValidator() {
        startActivity(Intent(this, ValidatorActivity::class.java))
        finish()
    }

    /**
     * Opens AdminActivity
     */
    private fun openAdmin() {
        startActivity(Intent(this, AdminActivity::class.java))
        finish()
    }
}