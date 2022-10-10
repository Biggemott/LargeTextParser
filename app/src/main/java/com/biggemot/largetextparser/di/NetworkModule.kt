package com.biggemot.largetextparser.di

import com.biggemot.largetextparser.data.ParserApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {
    companion object {

        private const val BASE_URL = "http://example.com/"

        @Provides
        fun provideClient(): OkHttpClient {
            return OkHttpClient.Builder().apply {
                addInterceptor(
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS)
                )
            }.build()
        }

        @Provides
        fun provideGson(): Gson {
            return GsonBuilder().create()
        }

        @Provides
        fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .build()

        @Provides
        fun provideChartApi(retrofit: Retrofit): ParserApi = retrofit.create(ParserApi::class.java)
    }
}