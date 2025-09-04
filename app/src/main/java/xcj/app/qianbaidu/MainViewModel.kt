package xcj.app.qianbaidu

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import xcj.app.qianbaidu.data.model.AIModel
import xcj.app.qianbaidu.data.model.QA
import xcj.app.qianbaidu.data.model.QAState

import javax.inject.Inject

import xcj.app.qianbaidu.data.repository.LocalQARepository
import xcj.app.qianbaidu.data.repository.RemoteDataRepository

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class MainViewModel @Inject constructor(
    private val localQARepository: LocalQARepository,
    private val remoteDataRepository: RemoteDataRepository
) : ViewModel() {
    private val TAG = "MainViewModel"

    private val _aqHistory: MutableList<QA> = mutableStateListOf()
    val qaHistory: List<QA> = _aqHistory

    private val _currentQAState = MutableStateFlow<QAState>(QAState.Init)
    val currentQAState: StateFlow<QAState> = _currentQAState.asStateFlow()

    private val _availableAIModels: MutableList<AIModel> = mutableStateListOf()

    val availableAIModels: List<AIModel> = _availableAIModels

    init {
        viewModelScope.launch {
            localQARepository.getAllQas().collectLatest { qasFromDb ->
                val map = _aqHistory.associateBy { it.id }
                qasFromDb.forEach { qa ->
                    if (!map.contains(qa.id)) {
                        _aqHistory.add(qa)
                    }
                }
            }
        }
        viewModelScope.launch {
            remoteDataRepository.listModels().onSuccess { remoteAIModels ->
                remoteAIModels.mapTo(_availableAIModels) { remoteAIModel ->
                    AIModel.Remote(
                        remoteAIModel
                    )
                }
            }
        }
    }

    fun processUserInput(aiModel: AIModel, content: String) {
        viewModelScope.launch {
            _currentQAState.value = QAState.InResponse(QA(question = content))
            val result =
                remoteDataRepository.ask(aiModel, content) // Still using DataRepository to fetch
            // Assuming ApiResponse.data contains the markdown string
            result.onSuccess { aiAnswer ->
                val qa = QA(question = content, answer = aiAnswer)
                addToInMemoryQAHistory(qa)
                _currentQAState.value = QAState.ResponseDone(qa)
            }.onFailure { throwable ->
                val qa = QA(question = content, answer = "Error: ${throwable.message}")
                addToInMemoryQAHistory(qa)
                _currentQAState.value = QAState.ResponseDone(qa)
            }
        }
    }

    fun addToInMemoryQAHistory(qa: QA) {
        //_aqHistory.add(qa) // Add to in-memory list for immediate UI update
        viewModelScope.launch {
            localQARepository.insertQa(qa) // Save to Room database
        }
    }

    fun showQA(qa: QA) {
        _currentQAState.value = QAState.ResponseDone.fromQA(qa)
    }

    fun clearHistory() {
        viewModelScope.launch {
            localQARepository.deleteAllQas()
            _aqHistory.clear()
        }
    }
}
