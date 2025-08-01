package ucne.edu.fintracker.data.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ucne.edu.fintracker.presentation.remote.FinTrackerApi
import javax.inject.Singleton
import ucne.edu.fintracker.presentation.remote.LocalDateTimeAdapter
import java.util.concurrent.TimeUnit

@InstallIn(SingletonComponent::class)
@Module
object ApiModule {
    const val BASE_URL =  "https://fintrackerapp.azurewebsites.net/"

    @Provides
    @Singleton
    fun providesMoshi(): Moshi =
        Moshi.Builder()
            .add(LocalDateTimeAdapter())      // Aquí agregas tu adapter
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    fun providesFinTrackerApi(moshi: Moshi): FinTrackerApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(FinTrackerApi::class.java)
    }

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

}


//    val moshi = Moshi.Builder()
//        .add(LocalDateTimeAdapter())
//        .build()
//
//
////    val retrofit = Retrofit.Builder()
////        .baseUrl("https://api.tuservicio.com/")
////        .addConverterFactory(MoshiConverterFactory.create(moshi))
////        .build()
//
//}