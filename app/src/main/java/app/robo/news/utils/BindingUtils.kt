package app.robo.news.utils

import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import app.robo.news.R
import com.bumptech.glide.Glide


private val bindingUtilsTAG: String = "BindingUtils"

interface BindableAdapter<T> {
    fun setData(data: T)
}

interface BindableSelectedAdapter<T> {
    fun setData(data: T, selectedPos: Int)
}


@BindingAdapter("data")
fun <T> setViewPagerProperties(viewPager2: ViewPager2, data: T) {
    if (viewPager2.adapter is BindableAdapter<*>) {
        (viewPager2.adapter as BindableAdapter<T>).setData(data)
    }
}

@BindingAdapter("data")
fun <T> setRecyclerViewProperties(recyclerView: RecyclerView, data: T) {
    if (recyclerView.adapter is BindableAdapter<*>) {
        (recyclerView.adapter as BindableAdapter<T>).setData(data)
    }
}

@BindingAdapter("app:full_image_url")
fun setFullImage(image: ImageView, path: String?) {
    val placeHolder = ColorDrawable(ContextCompat.getColor(image.context, R.color.gray))
    if (!path.isNullOrEmpty()) {
        Glide.with(image.context).load(path).placeholder(placeHolder)
            .error(placeHolder).into(image)
    } else {
        Glide.with(image.context).load(placeHolder).into(image)
    }
}

@BindingAdapter("app:image_url")
fun setImage(image: ImageView, path: String?) {
    val placeHolder = ColorDrawable(ContextCompat.getColor(image.context, R.color.gray))
    if (!path.isNullOrEmpty()) {
        val imageUrl = path

        Glide.with(image.context).load(imageUrl)
            .placeholder(placeHolder)
            .error(placeHolder).centerCrop()
            .into(image)
    } else {
        Glide.with(image.context).load(placeHolder).centerCrop().into(image)
    }
}