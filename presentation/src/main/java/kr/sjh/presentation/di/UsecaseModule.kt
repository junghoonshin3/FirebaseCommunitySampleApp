package kr.sjh.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.sjh.domain.repository.BoardRepository
import kr.sjh.domain.repository.LoginRepository
import kr.sjh.domain.usecase.board.CreatePostUseCase
import kr.sjh.domain.usecase.board.ReadPostsUseCase
import kr.sjh.domain.usecase.login.firebase.CreateUserUseCase
import kr.sjh.domain.usecase.login.firebase.DeleteUserUseCase
import kr.sjh.domain.usecase.login.firebase.ReadUserUseCase
import kr.sjh.domain.usecase.login.kakao.GetKakaoUserInfoUseCase
import kr.sjh.domain.usecase.login.kakao.LoginForKakaoUseCase
import kr.sjh.domain.usecase.login.kakao.LogoutKakaoUseCase
import kr.sjh.domain.usecase.login.kakao.ValidateKakaoAccessTokenUseCase

@Module
@InstallIn(SingletonComponent::class)
object PresentationModule {

    @Provides
    fun provideLoginForKakao(login: LoginRepository): LoginForKakaoUseCase {
        return LoginForKakaoUseCase(login)
    }

    @Provides
    fun provideValidateToken(login: LoginRepository): ValidateKakaoAccessTokenUseCase {
        return ValidateKakaoAccessTokenUseCase(login)
    }

    @Provides
    fun provideGetKakaoUserInfo(login: LoginRepository): GetKakaoUserInfoUseCase {
        return GetKakaoUserInfoUseCase(login)
    }

    @Provides
    fun provideLogoutKakao(login: LoginRepository): LogoutKakaoUseCase {
        return LogoutKakaoUseCase(login)
    }

    @Provides
    fun provideCreateUser(login: LoginRepository): CreateUserUseCase {
        return CreateUserUseCase(login)
    }

    @Provides
    fun provideDeleteUser(login: LoginRepository): DeleteUserUseCase {
        return DeleteUserUseCase(login)
    }

    @Provides
    fun provideReadUser(login: LoginRepository): ReadUserUseCase {
        return ReadUserUseCase(login)
    }

    @Provides
    fun provideReadPostsUseCase(board: BoardRepository): ReadPostsUseCase {
        return ReadPostsUseCase(board)
    }

    @Provides
    fun provideCreatePostsUseCase(board: BoardRepository): CreatePostUseCase {
        return CreatePostUseCase(board)
    }
}