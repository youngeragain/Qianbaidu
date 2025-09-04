package xcj.app.qianbaidu.data.model

import xcj.app.qianbaidu.data.model.IDProvider
import xcj.app.qianbaidu.data.model.LocalAIModel
import xcj.app.qianbaidu.data.model.RemoteAIModel

sealed interface AIModel: IDProvider {
    override val id: String
    val name: String

    data class Local(val ref: LocalAIModel) : AIModel {
        override val id: String
            get() = ref.id
        override val name: String
            get() = ref.name
    }

    data class Remote(val ref: RemoteAIModel) : AIModel {
        override val id: String
            get() = ref.id
        override val name: String
            get() = ref.name
    }
}