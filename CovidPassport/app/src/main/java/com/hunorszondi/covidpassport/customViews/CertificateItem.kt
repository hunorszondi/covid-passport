package com.hunorszondi.covidpassport.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hunorszondi.covidpassport.R
import com.hunorszondi.covidpassport.utils.ResourceUtil
import kotlinx.android.synthetic.main.certificate_item.view.*

class CertificateItem : ConstraintLayout {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        View.inflate(context, R.layout.certificate_item, this)
        docNameTextView.text = ResourceUtil.instance.getString(R.string.name)
    }

    fun setCertificateName(name: String) {
        docNameTextView.text = name
    }

    fun setImage(imageUrl: String?) {
        if( imageUrl == null) {
            Glide.with(context)
                .load(R.drawable.ic_profile_placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(imageViewRightBubble)
            return
        }
        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_profile_placeholder)
            .error(R.drawable.ic_profile_placeholder)
            .fallback(R.drawable.ic_profile_placeholder)
            .apply(RequestOptions.circleCropTransform())
            .into(imageViewRightBubble)
    }
}