package app.robo.news.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.io.IOException


const val TAG = "Utils"

fun <T : Any?> MutableLiveData<T>.default(initialValue: T) = apply { setValue(initialValue) }

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

fun Context.getJsonDataFromAsset(fileName: String): String? {
    val jsonString: String
    try {
        jsonString = this.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }
    return jsonString
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

fun Context.isNetworkAvailable(): Boolean {
    var result = false
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    connectivityManager?.let {
        it.getNetworkCapabilities(connectivityManager.activeNetwork)?.apply {
            result = when {
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }
    }
    return result
}