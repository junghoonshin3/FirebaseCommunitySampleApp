package kr.sjh.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.sjh.domain.repository.firebase.UserRepository
import kr.sjh.domain.usecase.user.ExistUserUseCase
import kr.sjh.domain.usecase.user.GetCurrentUserUseCase
import kr.sjh.domain.usecase.user.GetUserUseCase
import kr.sjh.domain.usecase.user.SignUpUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserUseCaseModule {
    @Provides
    @Singleton
    fun provideAuthSignUpUseCase(userRepository: UserRepository): GetCurrentUserUseCase {
        return GetCurrentUserUseCase(userRepository::getCurrentUser)
    }


    @Provides
    @Singleton
    fun provideGetUserUseCase(userRepository: UserRepository): GetUserUseCase {
        return GetUserUseCase(userRepository::getUser)
    }

    @Provides
    @Singleton
    fun provideExistUserUseCase(userRepository: UserRepository): ExistUserUseCase {
        return ExistUserUseCase(userRepository::isUserExist)
    }

    @Provides
    @Singleton
    fun provideSignUpUseCase(userRepository: UserRepository): SignUpUseCase {
        return SignUpUseCase(userRepository::signUp)
    }
}