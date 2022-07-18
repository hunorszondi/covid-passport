package com.hunorszondi.covidpassport.ui

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment

/**
 * Base class for all fragments used by this app
 */
open class BaseFragment: Fragment() {

    private lateinit var loading: LoadingDialog

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loading = LoadingDialog(requireContext(), layoutInflater)
    }

    override fun onResume() {
        super.onResume()
        (requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancelAll()
    }

    override fun onDetach() {
        super.onDetach()
        loading.cancelLoading()
    }

    /**
     * Starts loading animation
     */
    protected fun showLoading() {
        loading.showLoading()
    }

    /**
     * Stops loading animation
     */
    protected fun cancelLoading() {
        loading.cancelLoading()
    }

    protected fun getNavigationResult(key: String = "result") =
        NavHostFragment.findNavController(this).currentBackStackEntry?.savedStateHandle?.getLiveData<String>(key)

    protected fun setNavigationResult(result: String, key: String = "result") {
        NavHostFragment.findNavController(this).previousBackStackEntry?.savedStateHandle?.set(key, result)
    }
}