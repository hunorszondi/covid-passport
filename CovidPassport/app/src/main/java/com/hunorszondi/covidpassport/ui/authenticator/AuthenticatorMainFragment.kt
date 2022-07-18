package com.hunorszondi.covidpassport.ui.authenticator


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.hunorszondi.covidpassport.R
import com.hunorszondi.covidpassport.utils.ResourceUtil
import com.hunorszondi.covidpassport.Session
import com.hunorszondi.covidpassport.ui.BaseFragment
import com.hunorszondi.covidpassport.ui.common.ImageViewerFragment
import com.hunorszondi.covidpassport.viewModels.AuthenticatorViewModel
import kotlinx.android.synthetic.main.fragment_authenticator_main.*
import kotlinx.android.synthetic.main.fragment_authenticator_main.navigationHeader


/**
 * UI for Authenticator
 */
class AuthenticatorMainFragment : BaseFragment() {

    companion object {
        fun newInstance() = AuthenticatorMainFragment()
    }

    private lateinit var viewModel: AuthenticatorViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_authenticator_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(AuthenticatorViewModel::class.java)
        navigationHeader.setTitleVisibility(View.VISIBLE)
            .setTitle(ResourceUtil.instance.getString(R.string.authenticator))
            .setButtonVisibility(View.VISIBLE)
            .setButtonIcon(Session.instance.currentUser!!.photo?:R.drawable.ic_settings_black_24dp)
            .setButtonClickListener(View.OnClickListener {
                if(view != null){
                    Navigation.findNavController(requireView()).navigate(R.id.action_authenticatorMainFragment_to_profileFragment, null)
                }
            })

        searchButton.setOnClickListener {
            openQRCodeReader()
        }

        validLayout.visibility = View.GONE
        invalidLayout.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        val qrCodeResult = getNavigationResult()
        if (qrCodeResult?.value != null && qrCodeResult.value != "") {
            showLoading()
            viewModel.authenticateSignature(qrCodeResult.value!!) { success, message ->
                requireActivity().runOnUiThread {
                    cancelLoading()
                    if (success) {
                        showSuccess()
                    } else {
                        showFailed(message)
                    }
                }
            }
        }
    }

    private fun showFailed(message: String) {
        validLayout.visibility = View.GONE
        invalidLayout.visibility = View.VISIBLE
        errorTextView.text = message
    }

    private fun showSuccess() {
        validLayout.visibility = View.VISIBLE
        invalidLayout.visibility = View.GONE
        resultTextView.text = viewModel.currentVerifiedCertificate.toString()

        if (viewModel.currentVerifiedCertificate?.photo != null) {
            Glide.with(requireContext())
                .load(viewModel.currentVerifiedCertificate?.photo)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .fallback(R.drawable.ic_profile_placeholder)
                .into(profilePicture)
            profilePictureCardView.setOnClickListener { openImage(viewModel.currentVerifiedCertificate?.photo!!) }
        }

        if (viewModel.currentVerifiedCertificate?.idPhoto != null) {
            Glide.with(requireContext())
                .load(viewModel.currentVerifiedCertificate?.idPhoto)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .fallback(R.drawable.ic_profile_placeholder)
                .into(idImageView)
            idImageView.setOnClickListener { openImage(viewModel.currentVerifiedCertificate?.idPhoto!!) }
        }

        if (viewModel.currentVerifiedCertificate?.docPhoto != null) {
            Glide.with(requireContext())
                .load(viewModel.currentVerifiedCertificate?.docPhoto)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .fallback(R.drawable.ic_profile_placeholder)
                .into(docImageView)
            docImageView.setOnClickListener { openImage(viewModel.currentVerifiedCertificate?.docPhoto!!) }
        }
    }

    /**
     * Opens the qr cod reader
     */
    private fun openQRCodeReader() {
        if(view != null){
            Navigation.findNavController(requireView()).navigate(R.id.action_authenticatorMainFragment_to_qrCodeReaderFragment, null)
        }
    }

    /**
     * Opens ImageViewerFragment
     *
     * @param imageUrl image to be shown in ImageViewerFragment
     */
    private fun openImage(imageUrl: String) {
        val bundle = bundleOf(ImageViewerFragment.IMAGE_URL_ARG_KEY to imageUrl)
        if(view != null){
            Navigation.findNavController(requireView()).navigate(
                R.id.action_authenticatorMainFragment_to_imageViewerFragment,
                bundle
            )
        }
    }
}
