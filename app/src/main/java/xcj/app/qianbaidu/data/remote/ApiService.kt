package xcj.app.qianbaidu.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import xcj.app.qianbaidu.data.model.RemoteAIModel

interface ApiService {
    @POST("/ask") // 您可以根据需要更改端点
    suspend fun ask(@Body body: MutableMap<String, Any?>): ApiResponse<String>

    @POST("/ask_mock") // 您可以根据需要更改端点
    suspend fun askMock(@Body body: MutableMap<String, Any?>): ApiResponse<String>


    @GET("/list_models") // 您可以根据需要更改端点
    suspend fun listModels(): ApiResponse<List<RemoteAIModel>>
}