package xcj.app.qianbaidu.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "qa_table")
data class QA(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    val question: String,
    val answer: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) : IDProvider