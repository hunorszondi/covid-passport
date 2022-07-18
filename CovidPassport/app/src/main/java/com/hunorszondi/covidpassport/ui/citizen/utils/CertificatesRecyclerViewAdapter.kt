package com.hunorszondi.covidpassport.ui.citizen.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hunorszondi.covidpassport.R
import com.hunorszondi.covidpassport.model.apiModels.VaccineCertificateModel
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.certificate_item.view.*

/**
 * Adapter for certificate list
 */
class CertificatesRecyclerViewAdapter(
    private var vaccineCertificates: MutableList<VaccineCertificateModel>,
    private val certificateClicked:  (VaccineCertificateModel)->Unit,
    private val editClicked:  (VaccineCertificateModel)->Unit
) : RecyclerView.Adapter<CertificatesRecyclerViewAdapter.CertificateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.certificate_item, parent, false)
        return CertificateViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CertificateViewHolder, position: Int) {
        val vaccineCertificate: VaccineCertificateModel = vaccineCertificates[position]
        holder.bind(vaccineCertificate)
        holder.itemView.setOnClickListener { certificateClicked(vaccineCertificate) }
        holder.itemView.edit_icon.setOnClickListener { editClicked(vaccineCertificate) }
    }

    override fun getItemCount(): Int = vaccineCertificates.size

    fun updateList(vaccineCertificates: MutableList<VaccineCertificateModel>) {
        this.vaccineCertificates = vaccineCertificates
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        vaccineCertificates.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, vaccineCertificates.size)
    }

    class CertificateViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private var textViewName: TextView? = null
        private var imageView: ImageView? = null
        private var textViewStatus: TextView? = null
        var viewBackground: RelativeLayout? = null
        var viewForeground:ConstraintLayout? = null

        init {
            textViewName = itemView.findViewById(R.id.docNameTextView)
            textViewStatus = itemView.findViewById(R.id.statusTextView)
            imageView = itemView.findViewById(R.id.imageViewRightBubble)
            viewBackground = itemView.findViewById(R.id.view_background)
            viewForeground = itemView.findViewById(R.id.view_foreground)
        }

        fun bind(vaccineCertificate: VaccineCertificateModel) {
            textViewName!!.text = vaccineCertificate.docName
            textViewStatus!!.text = vaccineCertificate.status
//            if( vaccineCertificate.details.photo == null) {
//                Glide.with(itemView.context)
//                    .load(R.drawable.ic_profile_placeholder)
//                    .apply(RequestOptions.circleCropTransform())
//                    .into(imageView!!)
//                return
//            }
            Glide.with(itemView.context)
                .load(R.mipmap.ic_vaccine)
                .placeholder(R.mipmap.ic_vaccine)
                .error(R.mipmap.ic_vaccine)
                .fallback(R.mipmap.ic_vaccine)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView!!)
        }
    }

}
