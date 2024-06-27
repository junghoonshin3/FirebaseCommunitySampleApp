package kr.sjh.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.sjh.domain.repository.firebase.AuthRepository
import kr.sjh.domain.usecase.auth.firebase.AuthLogOutUseCase
import kr.sjh.domain.usecase.auth.firebase.AuthSignInUseCase
import kr.sjh.domain.usecase.auth.firebase.GetAuthCurrentUserUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthUseCaseModule {
    @Provides
    @Singleton
    fun provideAuthLogOutUseCase(
        authRepository: AuthRepository
    ): AuthLogOutUseCase {
        return AuthLogOutUseCase(authRepository::logOut)
    }


    @Provides
    @Singleton
    fun provideGetAuthCurrentUserUseCase(authRepository: AuthRepository): GetAuthCurrentUserUseCase {
        return GetAuthCurrentUserUseCase(authRepository::getCurrentAuthUser)
    }

    @Provides
    @Singleton
    fun provideAuthSignInUseCase(
        authRepository: AuthRepository
    ): AuthSignInUseCase {
        return AuthSignInUseCase(
            authRepository::signIn
        )
    }
}