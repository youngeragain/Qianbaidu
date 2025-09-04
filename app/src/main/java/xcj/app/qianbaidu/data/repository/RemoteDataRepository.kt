package xcj.app.qianbaidu.data.repository

import xcj.app.qianbaidu.data.model.AIModel
import xcj.app.qianbaidu.data.model.RemoteAIModel
import xcj.app.qianbaidu.data.remote.ApiService
import xcj.app.qianbaidu.data.remote.pareToResult

class RemoteDataRepository(
    private val apiService: ApiService
) {
    suspend fun ask(aiModel: AIModel, content: String): Result<String> {
        return try {
            val body = mutableMapOf<String, Any?>(
                "model" to aiModel.id,
                "question" to content
            )
            apiService.ask(body).pareToResult()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun listModels(): Result<List<RemoteAIModel>> {
        return try {
            apiService.listModels().pareToResult()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}