package app.robo.news.utils

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<Event<T>>.observeEvent(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner) { event ->
        event?.getContentIfNotHandled()?.let { observer.onChanged(it) }
    }
}

fun View.visible() {
    if (this != null) {
        visibility = View.VISIBLE
    }
}

fun View.isVisible(): Boolean {
    return visibility == View.VISIBLE
}

fun View.gone() {
    if (this != null) {
        visibility = View.GONE
    }
}

fun Context.toast(message: String?) {
    if (this != null) {
        this.toast(message, Toast.LENGTH_SHORT)
    }
}

fun Context.toast(message: String?, duration: Int) {
    if (this != null && !TextUtils.isEmpty(message) && message!!.isNotEmpty()) {
        try {
            Toast.makeText(this, message, duration).show()
        } catch (e: WindowManager.BadTokenException) {

        } catch (e: Exception) {

        }
    }
}