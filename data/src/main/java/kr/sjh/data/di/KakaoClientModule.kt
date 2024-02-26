package kr.sjh.data.di

import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class KakaoClientModule {
    @Provides
    @Singleton
    fun provideAuthApiClient() = AuthApiClient.instance

    @Provides
    @Singleton
    fun provideUserApiClient() = UserApiClient.instance
}