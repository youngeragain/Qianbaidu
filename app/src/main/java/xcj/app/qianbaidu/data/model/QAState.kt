package xcj.app.qianbaidu.data.model

sealed interface QAState {
    interface QAProvider : QAState {
        val qa: QA
    }

    object Init : QAState
    data class InResponse(override val qa: QA) : QAProvider
    data class ResponseDone(override val qa: QA) : QAProvider {
        companion object {
            fun fromQA(qa: QA): ResponseDone {
                return ResponseDone(qa)
            }
        }
    }
}