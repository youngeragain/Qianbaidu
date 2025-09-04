package xcj.app.qianbaidu.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import xcj.app.qianbaidu.data.model.QA
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.java

@Dao
interface QADao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qa: QA)

    @Update
    suspend fun update(qa: QA)

    @Delete
    suspend fun delete(qa: QA)

    @Query("DELETE FROM qa_table")
    suspend fun deleteAllQas()

    @Query("SELECT * FROM qa_table ORDER BY timestamp DESC")
    fun getAllQas(): Flow<List<QA>>

    @Query("SELECT * FROM qa_table WHERE id = :id")
    fun getQaById(id: String): Flow<QA?>
}