package xcj.app.qianbaidu.di

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import xcj.app.qianbaidu.data.local.AppDatabase
import xcj.app.qianbaidu.data.local.QADao
import xcj.app.qianbaidu.data.repository.LocalQARepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import xcj.app.qianbaidu.data.remote.ApiService
import xcj.app.qianbaidu.data.repository.RemoteDataRepository
import java.time.Duration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideQaDao(appDatabase: AppDatabase): QADao {
        return appDatabase.qaDao()
    }

    @Singleton
    @Provides
    fun provideLocalQARepository(qaDao: QADao): LocalQARepository {
        return LocalQARepository(qaDao)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(Duration.ofSeconds(30, 0))
            .readTimeout(Duration.ofSeconds(30, 0))
            .build()
        return okHttpClient
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("http://192.168.2.248:5000/") // 请替换为您的 API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }

    @Singleton
    @Provides
    fun provideRemoteDataRepository(retrofit: Retrofit): RemoteDataRepository {
        val apiService: ApiService = retrofit.create<ApiService>()
        return RemoteDataRepository(apiService)
    }
}