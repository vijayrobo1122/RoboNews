package app.robo.news.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers

const val dasTAG: String = "DataAccessStrategy"

fun <T> performNetworkOperation(networkCall: suspend () -> ApiResult<T>): LiveData<ApiResult<T>> =
    liveData(Dispatchers.IO) {
        emit(ApiResult.loading())
        val responseStatus = networkCall.invoke()
        if (responseStatus.status == Status.SUCCESS) {
            emit(
                ApiResult.success(
                    responseStatus.data!!,
                    responseStatus.message,
                    responseStatus.code
                )
            )
        } else if (responseStatus.status == Status.ERROR) {
            emit(ApiResult.error(responseStatus.message!!, responseStatus.code))
        }
    }