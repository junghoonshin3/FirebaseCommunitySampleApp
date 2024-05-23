package kr.sjh.domain.usecase.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.sjh.domain.repository.BoardRepository
import kr.sjh.domain.repository.KaKaoLoginRepository
import kr.sjh.domain.repository.LoginRepository
import kr.sjh.domain.usecase.board.CreatePostUseCase
import kr.sjh.domain.usecase.board.DeletePostUseCase
import kr.sjh.domain.usecase.board.ReadPostsUseCase
import kr.sjh.domain.usecase.login.firebase.CreateUserUseCase
import kr.sjh.domain.usecase.login.firebase.DeleteUserUseCase
import kr.sjh.domain.usecase.login.firebase.ReadUserUseCase
import kr.sjh.domain.usecase.login.firebase.UpdateUserUseCase
import kr.sjh.domain.usecase.login.kakao.KaKaoLogOutUseCase
import kr.sjh.domain.usecase.login.kakao.KaKaoLoginUseCase

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideKaKaoLogOut(repository: KaKaoLoginRepository): KaKaoLogOutUseCase {
        return KaKaoLogOutUseCase(repository)
    }

    @Provides
    fun provideKaKaoLogin(repository: KaKaoLoginRepository): KaKaoLoginUseCase {
        return KaKaoLoginUseCase(repository)
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


    @Provides
    fun provideDeletePostUseCase(board: BoardRepository): DeletePostUseCase {
        return DeletePostUseCase(board)
    }

    @Provides
    fun provideUpdateUserUseCase(login: LoginRepository): UpdateUserUseCase {
        return UpdateUserUseCase(login)
    }

}