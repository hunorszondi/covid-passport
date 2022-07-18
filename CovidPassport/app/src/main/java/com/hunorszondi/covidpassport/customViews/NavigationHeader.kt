package com.hunorszondi.covidpassport.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hunorszondi.covidpassport.R
import com.hunorszondi.covidpassport.utils.ResourceUtil
import kotlinx.android.synthetic.main.navigation_header_view.view.*

/**
 * Custom navigation header used in the app.
 */
class NavigationHeader : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        View.inflate(getContext(), R.layout.navigation_header_view, this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.NavigationHeader, 0, 0)
        val titleText = attributes.getString(R.styleable.NavigationHeader_title)
        if (titleText == null) {
            navigationHeaderTitleTextView.text = ResourceUtil.instance.getString(R.string.title)
        } else {
            navigationHeaderTitleTextView.text = titleText
        }
        attributes.recycle()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    /**
     * Sets the visibility of the navigation header title
     * @param visibility Has to be a View.VISIBLE/View.INVISIBLE/View.GONE
     * @return navigation header
     */
    fun setTitleVisibility(visibility: Int): NavigationHeader {
        navigationHeaderTitleTextView.visibility = visibility
        return this
    }

    /**
     * Sets the navigation header title
     * @param text any text
     * @return navigation header
     */
    fun setTitle(text: String): NavigationHeader {
        navigationHeaderTitleTextView.text = text
        return this
    }

    /**
     * Sets the visibility of the navigation header button
     * @param visibility Has to be a View.VISIBLE/View.INVISIBLE/View.GONE
     * @return navigation header
     */
    fun setButtonVisibility(visibility: Int): NavigationHeader {
        navigationHeaderButton.visibility = visibility
        return this
    }

    /**
     * Sets listener for the navigation header button
     * @param listener Has to be an instance of View.OnClickListener
     * @return navigation header
     */
    fun setButtonClickListener(listener: View.OnClickListener): NavigationHeader {
        navigationHeaderButton.setOnClickListener(listener)
        return this
    }

    /**
     * Sets the icon of the navigation header button
     * @param icon Has to be a drawable resource or url, path ex.: R.drawable.id
     * @return navigation header
     */
    fun <T>setButtonIcon(icon: T): NavigationHeader {
        Glide
            .with(this)
            .load(icon)
            .apply(RequestOptions.circleCropTransform())
            .into(navigationHeaderButton)
        return this
    }
}