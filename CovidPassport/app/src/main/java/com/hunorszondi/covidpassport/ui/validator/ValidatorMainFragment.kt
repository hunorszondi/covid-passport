package com.hunorszondi.covidpassport.ui.validator


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.hunorszondi.covidpassport.R
import com.hunorszondi.covidpassport.utils.ResourceUtil
import com.hunorszondi.covidpassport.Session
import com.hunorszondi.covidpassport.ui.BaseFragment
import com.hunorszondi.covidpassport.ui.common.ImageViewerFragment
import com.hunorszondi.covidpassport.viewModels.ValidatorViewModel
import kotlinx.android.synthetic.main.fragment_validator_main.*


/**
 * UI for Validator
 */
class ValidatorMainFragment : BaseFragment() {

    companion object {
        fun newInstance() = ValidatorMainFragment()
    }

    private lateinit var viewModel: ValidatorViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_validator_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(ValidatorViewModel::class.java)
        navigationHeader.setTitleVisibility(View.VISIBLE)
            .setTitle(ResourceUtil.instance.getString(R.string.validator))
            .setButtonVisibility(View.VISIBLE)
            .setButtonIcon(Session.instance.currentUser!!.photo?:R.drawable.ic_settings_black_24dp)
            .setButtonClickListener(View.OnClickListener {
                if(view != null){
                    Navigation.findNavController(requireView()).navigate(R.id.action_validatorMainFragment_to_profileFragment, null)
                }
            })

        acceptButton.setOnClickListener {
            validateCertificate(true)
        }

        declineButton.setOnClickListener {
            validateCertificate(false)
        }

        refreshButton.setOnClickListener {
            getNextCertificate()
        }

        if (viewModel.currentCertificate == null) {
            getNextCertificate()

            validLayout.visibility = View.GONE
            acceptButton.visibility = View.GONE
            declineButton.visibility = View.GONE
            noCertificatesTextView.visibility = View.GONE
            refreshButton.visibility = View.GONE
        } else {
            loadCertificate()
        }
    }

    private fun getNextCertificate() {
        showLoading()
        viewModel.getNextCertificate { success, message ->
            requireActivity().runOnUiThread {
                if (success) {
                    loadCertificate()
                } else {
                    validLayout.visibility = View.GONE
                    acceptButton.visibility = View.GONE
                    declineButton.visibility = View.GONE
                    noCertificatesTextView.visibility = View.VISIBLE
                    refreshButton.visibility = View.VISIBLE
                    if (message.isNotEmpty()) {
                        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                cancelLoading()
            }
        }
    }

    private fun loadCertificate() {
        resultTextView.text = viewModel.currentCertificate.toString()

        if (viewModel.currentCertificate?.photo != null) {
            Glide.with(requireContext())
                .load(viewModel.currentCertificate?.photo)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .fallback(R.drawable.ic_profile_placeholder)
                .into(profilePicture)
            profilePictureCardView.setOnClickListener { openImage(viewModel.currentCertificate?.photo!!) }
        }

        if (viewModel.currentCertificate?.idPhoto != null) {
            Glide.with(requireContext())
                .load(viewModel.currentCertificate?.idPhoto)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .fallback(R.drawable.ic_profile_placeholder)
                .into(idImageView)
            idImageView.setOnClickListener { openImage(viewModel.currentCertificate?.idPhoto!!) }
        }

        if (viewModel.currentCertificate?.docPhoto != null) {
            Glide.with(requireContext())
                .load(viewModel.currentCertificate?.docPhoto)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .fallback(R.drawable.ic_profile_placeholder)
                .into(docImageView)
            docImageView.setOnClickListener { openImage(viewModel.currentCertificate?.docPhoto!!) }
        }

        validLayout.visibility = View.VISIBLE
        acceptButton.visibility = View.VISIBLE
        declineButton.visibility = View.VISIBLE
        noCertificatesTextView.visibility = View.GONE
        refreshButton.visibility = View.GONE
    }

    /**
     * Send certificate validation response
     *
     * @param valid response
     */
    private fun validateCertificate(valid: Boolean) {
        viewModel.sendValidationResponse(valid) { success, message ->
            requireActivity().runOnUiThread {
                if (success) {
                    getNextCertificate()
                } else {
                    Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
                }
            }
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
                R.id.action_validatorMainFragment_to_imageViewerFragment,
                bundle
            )
        }
    }
}
