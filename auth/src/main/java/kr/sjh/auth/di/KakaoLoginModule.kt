package kr.sjh.auth.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kr.sjh.domain.kakao.KakaoLoginManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class KakaoLoginModule {
    @Provides
    @Singleton
    fun provideKakaoLoginManager(@ApplicationContext context: Context) =
        kr.sjh.domain.kakao.KakaoLoginManager(context)



}