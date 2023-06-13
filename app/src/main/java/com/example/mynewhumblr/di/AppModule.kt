package com.example.mynewhumblr.di

import com.example.mynewhumblr.api.HumblrApi
import com.example.mynewhumblr.api.UserApi
import com.example.mynewhumblr.data.interceptor.RetrofitInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://oauth.reddit.com/").client(RetrofitInterceptor.client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun HumblrApi(retrofit: Retrofit): HumblrApi =
        retrofit.create(HumblrApi::class.java)

    @Provides
    @Singleton
    fun UserApi(retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)
}