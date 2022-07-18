package com.hunorszondi.covidpassport.ui.citizen


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_certificate_manager.*
import android.content.DialogInterface
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.hunorszondi.covidpassport.R
import com.hunorszondi.covidpassport.model.apiModels.VaccineCertificateModel
import com.hunorszondi.covidpassport.ui.citizen.utils.CertificatesRecyclerViewAdapter
import com.hunorszondi.covidpassport.utils.ResourceUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.hunorszondi.covidpassport.Session
import com.hunorszondi.covidpassport.ui.BaseFragment
import com.hunorszondi.covidpassport.ui.citizen.utils.CertificateRecyclerViewItemHelper
import com.hunorszondi.covidpassport.utils.DialogFactory
import com.hunorszondi.covidpassport.viewModels.CitizenViewModel


/**
 * UI for Certificate
 */
class CertificateManagerFragment : BaseFragment(), CertificateRecyclerViewItemHelper.RecyclerItemTouchHelperListener {

    companion object {
        fun newInstance() = CertificateManagerFragment()
    }

    private lateinit var viewModel: CitizenViewModel
    private lateinit var certificateListAdapter: CertificatesRecyclerViewAdapter

    private val certificateListObserver = Observer<MutableList<VaccineCertificateModel>> { list ->
        val listToShow = list?: mutableListOf()
        if(listToShow.isEmpty()) {
            emptyListTextView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else{
            updateCertificateList(listToShow)
            emptyListTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_certificate_manager, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(CitizenViewModel::class.java)
        navigationHeader.setTitleVisibility(View.VISIBLE)
            .setTitle(ResourceUtil.instance.getString(R.string.certificates))
            .setButtonVisibility(View.VISIBLE)
            .setButtonIcon(Session.instance.currentUser!!.photo?:R.drawable.ic_settings_black_24dp)
            .setButtonClickListener(View.OnClickListener {
                if(view != null){
                    Navigation.findNavController(requireView()).navigate(R.id.action_certificateManagerFragment_to_profileFragment, null)
                }
            })

        viewModel.vaccineCertificateList.observe(viewLifecycleOwner, certificateListObserver)

        showLoading()
        viewModel.fetchCertificates(false, ::defaultCallback)

        initRecyclerView()

        addCertificateButton.setOnClickListener { addCertificate() }
    }

    /**
     * Opens the selected certificate
     */
    private fun onCertificateClicked(vaccineCertificate: VaccineCertificateModel) {
        viewModel.currentCertificate = vaccineCertificate
        viewModel.vaccineEditFragmentMode = VaccineFragment.Companion.MODES().INFO
        if(view != null){
            Navigation.findNavController(requireView()).navigate(R.id.action_certificateManagerFragment_to_vaccineEditFragment, null)
        }
    }

    /**
     * Opens the selected certificate in edit mode
     */
    private fun onCertificateEditClicked(vaccineCertificate: VaccineCertificateModel) {
        viewModel.currentCertificate = vaccineCertificate
        viewModel.vaccineEditFragmentMode = VaccineFragment.Companion.MODES().EDIT
        if(view != null){
            Navigation.findNavController(requireView()).navigate(R.id.action_certificateManagerFragment_to_vaccineEditFragment, null)
        }
    }

    /**
     * Initializing recycler view
     */
    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        certificateListAdapter = CertificatesRecyclerViewAdapter(ArrayList(), ::onCertificateClicked, ::onCertificateEditClicked)
        val itemTouchHelperCallback = CertificateRecyclerViewItemHelper(this, 0, ItemTouchHelper.LEFT)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = certificateListAdapter
        }
    }

    /**
     * Updating certificate list
     *
     * @param list to update with
     */
    private fun updateCertificateList(list: MutableList<VaccineCertificateModel>) {
        certificateListAdapter.updateList(list)
    }

    /**
     * Opens the screen where the user can add a new certificate
     */
    private fun addCertificate() {
        viewModel.currentCertificate = null
        viewModel.vaccineEditFragmentMode = VaccineFragment.Companion.MODES().ADD
        if(view != null){
            Navigation.findNavController(requireView()).navigate(R.id.action_certificateManagerFragment_to_vaccineEditFragment, null)
        }
    }

    /**
     * Deletes a certificate on swipe
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        val dialog = DialogFactory.makeMessage(requireContext(),
            ResourceUtil.instance.getString(R.string.delete_certifcate),
            ResourceUtil.instance.getString(R.string.are_you_sure_delete_certificate),
            ResourceUtil.instance.getString(R.string.yes),
            ResourceUtil.instance.getString(R.string.no),
            DialogInterface.OnClickListener { _, _ ->
                showLoading()
                viewModel.removeCertificateByPosition(position, ::defaultCallback)
            },
            DialogInterface.OnClickListener { _, _ -> certificateListAdapter.notifyItemChanged(position) })
        dialog.show()
    }

    /**
     * Updates UI after backend api response if needed
     *
     * @param status positive or negative update
     * @param message status description
     */
    private fun defaultCallback(status: Boolean, message: String) {
        requireActivity().runOnUiThread {
            cancelLoading()
            if(!status) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
