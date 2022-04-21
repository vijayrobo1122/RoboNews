package app.robo.news.di

import android.content.Context
import app.robo.news.BuildConfig
import app.robo.news.data.remote.ApiHeaderInterceptor
import app.robo.news.data.remote.ApiSource
import app.robo.news.data.repository.NewsRepository
import app.robo.news.ui.detail.DetailViewModel
import app.robo.news.ui.news.NewsViewModel
import app.robo.news.utils.CONNECTION_TIMEOUT
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private fun provideHttpInterceptor(): HttpLoggingInterceptor {
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

private fun provideApiHeaderInterceptor(appContext: Context): ApiHeaderInterceptor {
    return ApiHeaderInterceptor(appContext)
}

private fun provideOkHttpClient(
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

private fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}

private fun provideApiSource(retrofit: Retrofit): ApiSource {
    return retrofit.create(ApiSource::class.java)
}

private fun provideNewsRepository(apiSource: ApiSource): NewsRepository {
    return NewsRepository(apiSource)
}

val appModule = module {
    single { GsonBuilder().setPrettyPrinting().setLenient().create() }
    single { provideHttpInterceptor() }
    single { provideApiHeaderInterceptor(get()) }
    single { provideOkHttpClient(get(), get()) }
    single { provideRetrofit(get(), get()) }
    single { provideApiSource(get()) }
    single { provideNewsRepository(get()) }
    viewModel { NewsViewModel(get()) }
    viewModel { DetailViewModel(get()) }
}

