package com.hunorszondi.covidpassport.ui.common

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.hunorszondi.covidpassport.R
import com.hunorszondi.covidpassport.Session
import com.hunorszondi.covidpassport.model.apiModels.PermissionTypes

/**
 * Custom dialog layout for selecting access type
 */
class AccessSelector(context: Context, layoutInflater: LayoutInflater, resultCallback: (result: String) -> Unit, closeCallback: () -> Unit) {
    private var dialog: AlertDialog = AlertDialog.Builder(context).create()

    init {
        val permissions = Session.instance.currentUser?.permissions

        val dialogView = layoutInflater.inflate(R.layout.access_selector_dialog, null)
        dialog.setView(dialogView)

        val citizenButton = dialogView.findViewById(R.id.citizenButton) as Button
        val authenticatorButton = dialogView.findViewById(R.id.authenticatorButton) as Button
        val validatorButton = dialogView.findViewById(R.id.validatorButton) as Button
        val adminButton = dialogView.findViewById(R.id.adminButton) as Button
        val exitButton = dialogView.findViewById(R.id.exitButton) as Button

        if (permissions?.contains(PermissionTypes().CITIZEN) == true) {
            citizenButton.visibility = View.VISIBLE
            citizenButton.setOnClickListener {
                resultCallback(PermissionTypes().CITIZEN)
                dialog.cancel()
            }
        }

        if (permissions?.contains(PermissionTypes().AUTHENTICATOR) == true) {
            authenticatorButton.visibility = View.VISIBLE
            authenticatorButton.setOnClickListener {
                resultCallback(PermissionTypes().AUTHENTICATOR)
                dialog.cancel()
            }
        }

        if (permissions?.contains(PermissionTypes().VALIDATOR) == true) {
            validatorButton.visibility = View.VISIBLE
            validatorButton.setOnClickListener {
                resultCallback(PermissionTypes().VALIDATOR)
                dialog.cancel()
            }
        }

        if (permissions?.contains(PermissionTypes().ADMIN) == true) {
            adminButton.visibility = View.VISIBLE
            adminButton.setOnClickListener {
                resultCallback(PermissionTypes().ADMIN)
                dialog.cancel()
            }
        }

        exitButton.setOnClickListener {
            closeCallback()
            dialog.cancel()
        }

        dialog.setOnCancelListener {
            closeCallback()
            dialog.cancel()
        }
    }

    /**
     * Show dialog
     */
    fun show() {
        dialog.show()
    }
}