package com.hunorszondi.covidpassport.ui.auth

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.hunorszondi.covidpassport.R
import com.hunorszondi.covidpassport.Session
import com.hunorszondi.covidpassport.model.apiModels.PermissionTypes
import com.hunorszondi.covidpassport.ui.BaseFragment
import com.hunorszondi.covidpassport.ui.admin.AdminActivity
import com.hunorszondi.covidpassport.ui.authenticator.AuthenticatorActivity
import com.hunorszondi.covidpassport.ui.citizen.CitizenActivity
import com.hunorszondi.covidpassport.ui.common.AccessSelector
import com.hunorszondi.covidpassport.ui.validator.ValidatorActivity
import com.hunorszondi.covidpassport.viewModels.LoginViewModel
import kotlinx.android.synthetic.main.login_fragment.*

/**
 * UI of login screen
 */
class LoginFragment : BaseFragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(LoginViewModel::class.java)

        registerLink.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_registerFragment, null))
        loginButton.setOnClickListener {
            showLoading()
            viewModel.login(usernameInput.text.toString(), passwordInput.text.toString(), ::loginCallback)
        }
    }

    /**
     * Handles UI update if the viewModel requires it
     */
    private fun loginCallback(status: Boolean, message: String) {
        requireActivity().runOnUiThread {
            cancelLoading()
            if(status) {
                if (Session.instance.hasMultiplePermissions()) {
                    AccessSelector(requireContext(), this.layoutInflater, {
                        when(it) {
                            PermissionTypes().CITIZEN -> openCitizen()
                            PermissionTypes().AUTHENTICATOR -> openAuthenticator()
                            PermissionTypes().VALIDATOR -> openValidator()
                            PermissionTypes().ADMIN -> openAdmin()
                        }
                    }, {
                        requireActivity().finish()
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
                Toast.makeText(context, "Login failed: $message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Opens CitizenActivity -> CertificateManagerFragment
     */
    private fun openCitizen() {
        startActivity(Intent(requireActivity(), CitizenActivity::class.java))
        requireActivity().finish()
    }

    /**
     * Opens AuthenticatorActivity
     */
    private fun openAuthenticator() {
        startActivity(Intent(requireActivity(), AuthenticatorActivity::class.java))
        requireActivity().finish()
    }

    /**
     * Opens ValidatorActivity
     */
    private fun openValidator() {
        startActivity(Intent(requireActivity(), ValidatorActivity::class.java))
        requireActivity().finish()
    }

    /**
     * Opens AdminActivity
     */
    private fun openAdmin() {
        startActivity(Intent(requireActivity(), AdminActivity::class.java))
        requireActivity().finish()
    }

}
