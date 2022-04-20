package app.robo.news.data.remote

data class ApiResult<out T>(val status: Status, val data: T?, val message: String?, val code: Int) {
    companion object {
        fun <T> loading(data: T? = null, code: Int = 0): ApiResult<T> {
            return ApiResult(Status.LOADING, data, null, code)
        }

        fun <T> success(data: T?, message: String? = null, code: Int): ApiResult<T> {
            return ApiResult(Status.SUCCESS, data, message, code)
        }

        fun <T> error(message: String = "", code: Int, data: T? = null): ApiResult<T> {
            return ApiResult(Status.ERROR, data, message, code)
        }
    }
}