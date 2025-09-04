package xcj.app.qianbaidu.data.remote

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("code") val code: Int,
    @SerializedName("msg") val message: String,
    @SerializedName("data") val data: T?
)

fun <T> ApiResponse<T>.pareToResult(): Result<T> {
    return if (code == 0 && data != null) {
        Result.success(data)
    } else {
        Result.failure(Exception(message))
    }
}