package com.hunorszondi.covidpassport.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hunorszondi.covidpassport.R

/**
 * Run behind of every fragment related with authentication
 */
class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_activity)
    }

}
