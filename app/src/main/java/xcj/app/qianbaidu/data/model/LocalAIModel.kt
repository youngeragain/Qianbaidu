package xcj.app.qianbaidu.data.model

/**
 * @param type text, image, video, mixin
 */
data class LocalAIModel(
    val id: String,
    val name: String,
    val description: String,
    val type: String,
    val applicationType: String,
)