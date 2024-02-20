package kr.sjh.presentation.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kr.sjh.presentation.utill.LoginManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class KakaoLoginModule {
    @Provides
    @Singleton
    fun provideKakaoLoginModule(@ApplicationContext context: Context) = LoginManager(context)

}