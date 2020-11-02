package kz.bmukhtar.quotas.presentation.adapter

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import coil.target.ImageViewTarget
import kz.bmukhtar.quotas.R

private const val IMAGE_VISIBILITY_THRESHOLD = 8

class CustomImageViewTarget(
    override val view: ImageView,
    private val text: TextView
) : ImageViewTarget(view) {

    private val layoutParams = text.layoutParams as? ViewGroup.MarginLayoutParams
    private val tickerMarginImageVisible =
        text.context.resources.getDimension(R.dimen.margin_ticker_when_image_visible).toInt()
    private val tickerMarginImageInvisible =
        text.context.resources.getDimension(R.dimen.margin_ticker_when_image_invisible).toInt()

    override fun onError(error: Drawable?) {
        super.onError(error)
        setImageMarginStart(tickerMarginImageInvisible)
    }

    override fun onStart(placeholder: Drawable?) {
        super.onStart(placeholder)
        setImageMarginStart(tickerMarginImageInvisible)
    }

    override fun onSuccess(result: Drawable) {
        super.onSuccess(result)
        if (result.intrinsicWidth < IMAGE_VISIBILITY_THRESHOLD || result.intrinsicHeight < IMAGE_VISIBILITY_THRESHOLD) {
            setImageMarginStart(tickerMarginImageInvisible)
        } else {
            setImageMarginStart(tickerMarginImageVisible)
        }
    }

    private fun setImageMarginStart(margin: Int) {
        layoutParams?.marginStart = margin
        text.layoutParams = layoutParams
    }
}