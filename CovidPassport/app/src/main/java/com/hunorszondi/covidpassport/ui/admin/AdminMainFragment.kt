package com.hunorszondi.covidpassport.ui.admin


import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.hunorszondi.covidpassport.R
import com.hunorszondi.covidpassport.utils.ResourceUtil
import com.hunorszondi.covidpassport.Session
import com.hunorszondi.covidpassport.model.apiModels.PermissionTypes
import com.hunorszondi.covidpassport.ui.BaseFragment
import com.hunorszondi.covidpassport.utils.DialogFactory
import com.hunorszondi.covidpassport.viewModels.AdminViewModel
import kotlinx.android.synthetic.main.fragment_admin_main.*


/**
 * UI for Admin
 */
class AdminMainFragment : BaseFragment() {

    companion object {
        fun newInstance() = AdminMainFragment()
    }

    private lateinit var viewModel: AdminViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_admin_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(AdminViewModel::class.java)
        navigationHeader.setTitleVisibility(View.VISIBLE)
            .setTitle(ResourceUtil.instance.getString(R.string.admin))
            .setButtonVisibility(View.VISIBLE)
            .setButtonIcon(Session.instance.currentUser!!.photo?:R.drawable.ic_settings_black_24dp)
            .setButtonClickListener(View.OnClickListener {
                if(view != null){
                    Navigation.findNavController(requireView()).navigate(R.id.action_adminMainFragment_to_profileFragment, null)
                }
            })

        searchButton.setOnClickListener { searchUser() }
        updatePermissionsButton.setOnClickListener { updatePermissions() }

        deleteButton.setOnClickListener { DialogFactory.makeMessage(requireContext(),
            ResourceUtil.instance.getString(R.string.delete_user),
            ResourceUtil.instance.getString(R.string.are_you_sure_delete_user),
            ResourceUtil.instance.getString(R.string.yes),
            ResourceUtil.instance.getString(R.string.no),
            DialogInterface.OnClickListener { _, _ ->
                deleteUser()
            },
            DialogInterface.OnClickListener { _, _ -> }).show() }

        userSettingsLayout.visibility = View.GONE
        noUserTextView.visibility = View.GONE
    }

    private fun searchUser() {
        showLoading()
        viewModel.searchUser(searchInput.text.toString()) { success, message ->
            requireActivity().runOnUiThread {
                if (success) {
                    loadFoundUserInfo()
                } else {
                    userSettingsLayout.visibility = View.GONE
                    noUserTextView.visibility = View.VISIBLE
                }
                cancelLoading()
            }
        }
    }

    private fun loadFoundUserInfo() {
        if(viewModel.foundUser != null) {
            userTextView.text = viewModel.foundUser!!.userName

            adminSwitch.isChecked = false
            validatorSwitch.isChecked = false
            authenticatorSwitch.isChecked = false
            citizenSwitch.isChecked = false

            viewModel.foundUser!!.permissions.forEach {
                when(it) {
                    PermissionTypes().ADMIN -> adminSwitch.isChecked = true
                    PermissionTypes().VALIDATOR -> validatorSwitch.isChecked = true
                    PermissionTypes().AUTHENTICATOR -> authenticatorSwitch.isChecked = true
                    PermissionTypes().CITIZEN -> citizenSwitch.isChecked = true
                }
            }
        }

        userSettingsLayout.visibility = View.VISIBLE
        noUserTextView.visibility = View.GONE
    }

    private fun updatePermissions() {
        showLoading()

        val newPermissions = ArrayList<String>()
        if (adminSwitch.isChecked) {
            newPermissions.add(PermissionTypes().ADMIN)
        }
        if (validatorSwitch.isChecked) {
            newPermissions.add(PermissionTypes().VALIDATOR)
        }
        if (authenticatorSwitch.isChecked) {
            newPermissions.add(PermissionTypes().AUTHENTICATOR)
        }
        if (citizenSwitch.isChecked) {
            newPermissions.add(PermissionTypes().CITIZEN)
        }

        viewModel.updatePermissions(newPermissions) { success, message ->
            requireActivity().runOnUiThread {
                if (success) {
                    Toast.makeText(requireContext(), "User permissions updated", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    if (message.isNotEmpty()) {
                        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                cancelLoading()
            }
        }
    }

    private fun deleteUser() {
        showLoading()
        viewModel.deleteUserAccount { success, message ->
            requireActivity().runOnUiThread {
                if (success) {
                    userSettingsLayout.visibility = View.GONE
                    noUserTextView.visibility = View.GONE
                    Toast.makeText(requireContext(), "User deleted", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    if (message.isNotEmpty()) {
                        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                cancelLoading()
            }
        }
    }

}
