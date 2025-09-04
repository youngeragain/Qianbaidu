package xcj.app.qianbaidu.data.repository

import xcj.app.qianbaidu.data.model.QA
import xcj.app.qianbaidu.data.local.QADao
import kotlinx.coroutines.flow.Flow

class LocalQARepository(private val qaDao: QADao) {

    fun getAllQas(): Flow<List<QA>> = qaDao.getAllQas()

    fun getQaById(id: String): Flow<QA?> = qaDao.getQaById(id)

    suspend fun insertQa(qa: QA) {
        qaDao.insert(qa)
    }

    suspend fun updateQa(qa: QA) {
        qaDao.update(qa)
    }

    suspend fun deleteQa(qa: QA) {
        qaDao.delete(qa)
    }

    suspend fun deleteAllQas() {
        qaDao.deleteAllQas()
    }

}