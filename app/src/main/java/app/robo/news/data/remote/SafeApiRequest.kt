package app.robo.news.data.remote

import android.util.Log
import app.robo.news.R
import app.robo.news.RoboNewsApplication
import app.robo.news.data.model.api.BaseResponse
import app.robo.news.data.model.api.ErrorResponse
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

abstract class SafeApiRequest {

    companion object {
        private val TAG: String = SafeApiRequest::class.java.simpleName
    }

    suspend fun <T : Any> apiRequest(call: suspend () -> T): ApiResult<T> {
        return try {
            val response = call.invoke()
            handleSuccess(response)
        } catch (e: Exception) {
            e.printStackTrace()
            handleException(e)
        }
    }

    private fun <T : Any> handleSuccess(data: T): ApiResult<T> {
        var message = ""
        var code = -1
        if (data is BaseResponse) {
            code = data.code
            message = data.message
        }
        return ApiResult.success(data, message, code)
    }

    private fun <T : Any> handleException(e: Exception): ApiResult<T> {
        return when (e) {
            is UnknownHostException -> {
                Log.e(TAG, "handleException c1")
                ApiResult.error(
                    getErrorMessage(ErrorCodes.SocketTimeOut.code),
                    ErrorCodes.SocketTimeOut.code, null
                )
            }
            is ConnectException -> {
                Log.e(TAG, "handleException c2")
                ApiResult.error(
                    getErrorMessage(ErrorCodes.SocketTimeOut.code),
                    ErrorCodes.SocketTimeOut.code, null
                )
            }
            is SocketTimeoutException -> {
                Log.e(TAG, "handleException c3")
                ApiResult.error(
                    getErrorMessage(ErrorCodes.SocketTimeOut.code),
                    ErrorCodes.SocketTimeOut.code, null
                )
            }
            is TimeoutException -> {
                Log.e(TAG, "handleException c4")
                ApiResult.error(
                    getErrorMessage(ErrorCodes.SocketTimeOut.code),
                    ErrorCodes.SocketTimeOut.code, null
                )
            }
            is JsonSyntaxException -> {
                Log.e(TAG, "handleException c5")
                ApiResult.error(
                    "Json Syntax error " + e.message, ErrorCodes.JsonSyntax.code, null
                )
            }
            is JsonParseException -> {
                Log.e(TAG, "handleException c6")
                ApiResult.error(
                    "Json Parsing error " + e.message, ErrorCodes.JsonParse.code, null
                )
            }
            is IOException -> {
                Log.e(TAG, "handleException c7")
                ApiResult.error(e.message!!, ErrorCodes.IOError.code, null)
            }
            is HttpException -> {
                Log.e(TAG, "handleException c8")
                try {
                    if (e.response() != null && e.response()!!.errorBody() != null) {
                        Log.e(TAG, "handleException c8 1")
                        val body = e.response()!!.errorBody()
                        if (body == null) {
                            Log.e(TAG, "handleException c8 1 body null")
                            ApiResult.error(getErrorMessage(e.code()), e.code(), null)
                        }
                        val gson = Gson()
                        val type = object : TypeToken<ErrorResponse>() {}.type
                        var errorResponse: ErrorResponse? = null
                        errorResponse = try {
                            gson.fromJson(body!!.charStream(), type)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            gson.fromJson(body!!.string(), type)
                        }
                        if (errorResponse != null) {
                            Log.e(TAG, "handleException c81 errorResponse :$errorResponse")
                            if (errorResponse.code == 401) {
                                Log.e(TAG, "Unauthorised")
                            }
                            ApiResult.error(errorResponse.message, errorResponse.code, null)
                        } else {
                            Log.e(TAG, "handleException c8ww")
                            ApiResult.error(getErrorMessage(e.code()), e.code(), null)
                        }
                    } else {
                        Log.e(TAG, "handleException c8 2")
                        ApiResult.error(getErrorMessage(e.code()), e.code(), null)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "handleException c8 3")
                    ApiResult.error("${e.localizedMessage}", ErrorCodes.OtherError.code, null)
                }
            }
            else -> {
                Log.e(TAG, "handleException c9")
                ApiResult.error(
                    getErrorMessage(Int.MAX_VALUE), ErrorCodes.SomethingWentWrong.code, null
                )
            }
        }
    }

    private fun getErrorMessage(code: Int): String {
        return when (code) {
            ErrorCodes.SocketTimeOut.code -> {
                RoboNewsApplication.getInstance().applicationContext.getString(
                    R.string.internet_not_available
                )
            }
            ErrorCodes.BadRequest.code -> "Bad Request"
            ErrorCodes.Unauthorised.code -> "Unauthorised"
            ErrorCodes.PaymentRequired.code -> "Payment Required"
            ErrorCodes.Forbidden.code -> "Forbidden"
            ErrorCodes.NotFound.code -> "Not found"
            ErrorCodes.MethodNotAllowed.code -> "Method Not Allowed"
            ErrorCodes.NotAcceptable.code -> "Not Acceptable"
            ErrorCodes.ProxyAuthenticationRequired.code -> "Proxy Authentication Required"
            ErrorCodes.RequestTimeout.code -> "Request Timeout"
            ErrorCodes.Conflict.code -> "Conflict"
            ErrorCodes.Gone.code -> "Gone"
            ErrorCodes.LengthRequired.code -> "Length Required"
            ErrorCodes.PreconditionFailed.code -> "Precondition Failed"
            ErrorCodes.PayloadTooLarge.code -> "Payload Too Large"
            ErrorCodes.URITooLong.code -> "URI Too Long"
            ErrorCodes.UnsupportedMediaType.code -> "Unsupported Media Type"
            ErrorCodes.RequestedRangeNotSatisfiable.code -> "Requested Range Not Satisfiable"
            ErrorCodes.ExpectationFailed.code -> "Expectation Failed"
            ErrorCodes.IamATeapot.code -> "I'm a teapot"
            ErrorCodes.MisdirectedRequest.code -> "Misdirected Request"
            ErrorCodes.UnprocessableEntity.code -> "Unprocessable Entity"
            ErrorCodes.Locked.code -> "Locked"
            ErrorCodes.FailedDependency.code -> "Failed Dependency"
            ErrorCodes.TooEarly.code -> "Too Early"
            ErrorCodes.UpgradeRequired.code -> "Upgrade Required"
            ErrorCodes.PreconditionRequired.code -> "Precondition Required"
            ErrorCodes.TooManyRequests.code -> "Too Many Requests"
            ErrorCodes.RequestHeaderFieldsTooLarge.code -> "Request Header Fields Too Large"
            ErrorCodes.UnavailableForLegalReasons.code -> "Unavailable For Legal Reasons"
            ErrorCodes.InternalServerError.code -> "Internal Server Error"
            ErrorCodes.NotImplemented.code -> "Not Implemented"
            ErrorCodes.BadGateway.code -> "Bad Gateway"
            ErrorCodes.ServiceUnavailable.code -> "Service Unavailable"
            ErrorCodes.GatewayTimeout.code -> "Gateway Timeout"
            ErrorCodes.HTTPVersionNotSupported.code -> "HTTP Version Not Supported"
            ErrorCodes.VariantAlsoNegotiates.code -> "Variant Also Negotiates"
            ErrorCodes.InsufficientStorage.code -> "Insufficient Storage"
            ErrorCodes.LoopDetected.code -> "Loop Detected"
            ErrorCodes.NotExtended.code -> "Not Extended"
            ErrorCodes.NetworkAuthenticationRequired.code -> "Network Authentication Required"
            ErrorCodes.NetworkConnectTimeoutError.code -> "Network Connect Timeout Error"
            else -> "Something went wrong with code $code"
        }
    }

}