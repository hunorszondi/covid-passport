package com.hunorszondi.covidpassport.ui.common

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hunorszondi.covidpassport.R
import kotlinx.android.synthetic.main.fragment_image_view.*
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hunorszondi.covidpassport.ui.BaseFragment

/**
 * UI for displaying images
 */
class ImageViewerFragment : BaseFragment() {

    private var originalPicture: Bitmap? = null

    private lateinit var imageUrl: String

    companion object {
        const val IMAGE_URL_ARG_KEY: String = "IMAGE_URL_ARG_KEY"
        fun newInstance() = ImageViewerFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_image_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        imageUrl = arguments?.getString(IMAGE_URL_ARG_KEY)!!
        initView()
    }

    /**
     * Initializing view
     */
    private fun initView() {
        closeButton.setOnClickListener { requireActivity().onBackPressed() }
        loadImage()
    }

    /**
     * Loading image from local file or remote url
     */
    private fun loadImage() {
        if(imageUrl.contains(Regex("^(http|https)://"))) {
            Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        originalPicture = resource
                        originalImageView.setImageBitmap(resource)
                    }
                    override fun onLoadCleared(placeholder: Drawable?) { }
                })
        } else {
            originalPicture = BitmapFactory.decodeFile(imageUrl)
            originalImageView.setImageBitmap(originalPicture)
        }
    }
}