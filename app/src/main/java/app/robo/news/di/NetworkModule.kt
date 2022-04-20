package app.robo.news.di

import android.content.Context
import app.robo.news.BuildConfig
import app.robo.news.data.remote.ApiHeaderInterceptor
import app.robo.news.data.remote.ApiSource
import app.robo.news.data.repository.NewsRepository
import app.robo.news.utils.CONNECTION_TIMEOUT
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideApiSource(retrofit: Retrofit): ApiSource {
        return retrofit.create(ApiSource::class.java)
    }

    @Singleton
    @Provides
    fun provideHttpInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)
            /**
             * If you uncomment below line then application will crashing due to out of memory error
             */
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        return interceptor
    }

    @Singleton
    @Provides
    fun provideApiHeaderInterceptor(@ApplicationContext appContext: Context): ApiHeaderInterceptor {
        return ApiHeaderInterceptor(appContext)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        apiHeaderInterceptor: ApiHeaderInterceptor
    ): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.connectTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.MINUTES)
        okHttpClient.writeTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.MINUTES)
        okHttpClient.readTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.MINUTES)
        okHttpClient.addInterceptor(apiHeaderInterceptor)
        okHttpClient.addInterceptor(httpLoggingInterceptor)
        okHttpClient.retryOnConnectionFailure(true)
        return okHttpClient.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Singleton
    @Provides
    fun provideNewsRepository(
        @ApplicationContext context: Context,
        apiSource: ApiSource
    ): NewsRepository {
        return NewsRepository(context, apiSource)
    }

}