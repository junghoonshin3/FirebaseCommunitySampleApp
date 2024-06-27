package kr.sjh.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.sjh.domain.repository.preferences.DataStoreRepository
import kr.sjh.domain.usecase.preferences.GetPreferenceAsyncUseCase
import kr.sjh.domain.usecase.preferences.GetPreferenceSyncUseCase
import kr.sjh.domain.usecase.preferences.SavePreferenceUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferenceUseCaseModule {

    @Provides
    @Singleton
    fun provideGetPreferenceAsyncUseCase(dataStoreRepository: DataStoreRepository): GetPreferenceAsyncUseCase {
        return GetPreferenceAsyncUseCase(dataStoreRepository::getPreferenceAsync)
    }

    @Provides
    @Singleton
    fun provideGetPreferenceSyncUseCase(dataStoreRepository: DataStoreRepository): GetPreferenceSyncUseCase {
        return GetPreferenceSyncUseCase(dataStoreRepository::getPreferenceSync)
    }

    @Provides
    @Singleton
    fun provideSavePreferenceUseCase(dataStoreRepository: DataStoreRepository): SavePreferenceUseCase {
        return SavePreferenceUseCase(dataStoreRepository::savePreference)
    }
}
