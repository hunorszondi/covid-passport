package com.hunorszondi.covidpassport.ui

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.hunorszondi.covidpassport.R

/**
 * Custom dialog layout for loading screens
 */
class LoadingDialog(context: Context, layoutInflater: LayoutInflater) {
    private var dialog: AlertDialog = AlertDialog.Builder(context).create()
    private var isActive: Boolean = false

    init {
        val dialogView = layoutInflater.inflate(R.layout.loading_dialog, null)
        dialog.setView(dialogView)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    /**
     * Show loading
     */
    fun showLoading() {
        isActive = true
        dialog.show()
    }

    /**
     * Cancel loading
     */
    fun cancelLoading() {
        if(isActive) {
            isActive = false
            dialog.cancel()
        }
    }
}