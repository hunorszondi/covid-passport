package com.hunorszondi.covidpassport.utils

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface

import androidx.appcompat.app.AlertDialog
import com.hunorszondi.covidpassport.R

/**
 * Provides alert dialogs with different options
 */
class DialogFactory {

    companion object {
        fun makeMessage(context: Context, title: String, text: String): Dialog {
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle(title)
                .setMessage(text)
                .setPositiveButton(ResourceUtil.instance.getString(R.string.ok), null)
            return dialog.create()
        }

        fun makeMessage(
            context: Context, title: String, text: String,
            positiveText: String, negativetext: String,
            positive: DialogInterface.OnClickListener,
            negative: DialogInterface.OnClickListener
        ): Dialog {
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle(title)
                .setMessage(text)
                .setPositiveButton(positiveText, positive)
                .setNegativeButton(negativetext, negative)
            return dialog.create()
        }
    }
}