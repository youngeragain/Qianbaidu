package xcj.app.qianbaidu.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import xcj.app.qianbaidu.data.model.QA

@Database(entities = [QA::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun qaDao(): QADao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                // Add migrations here if you change the schema in the future
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}