package app.robo.news.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import app.robo.news.BuildConfig
import app.robo.news.R
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.ConnectException
import javax.inject.Inject

class ApiHeaderInterceptor @Inject constructor(@ApplicationContext private val appContext: Context) :
    Interceptor {

    companion object {
        private val TAG = ApiHeaderInterceptor::class.java.simpleName
        private const val HEADER_KEY_AUTHORIZATION = "Authorization"
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isInternetAvailable()) {
            throw ConnectException(appContext.getString(R.string.internet_not_available))
        }

        val request: Request = chain.request().newBuilder()
            .addHeader(HEADER_KEY_AUTHORIZATION, "Bearer " + BuildConfig.API_KEY)
            .build()

        return chain.proceed(request)
    }

    private fun isInternetAvailable(): Boolean {
        var result = false
        val connectivityManager =
            appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
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

}