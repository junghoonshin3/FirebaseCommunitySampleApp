package kr.sjh.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.sjh.domain.repository.firebase.PostRepository
import kr.sjh.domain.usecase.board.AddPostUseCase
import kr.sjh.domain.usecase.board.GetPostUseCase
import kr.sjh.domain.usecase.board.GetPostsUseCase
import kr.sjh.domain.usecase.board.RemovePostUseCase
import kr.sjh.domain.usecase.board.UpdatePostCountUseCase
import kr.sjh.domain.usecase.board.UpdatePostUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PostUseCaseModule {

    @Provides
    @Singleton
    fun provideGetPostsUseCase(postRepository: PostRepository): GetPostsUseCase {
        return GetPostsUseCase(postRepository::getPosts)
    }

    @Provides
    @Singleton
    fun provideGetPostUseCase(postRepository: PostRepository): GetPostUseCase {
        return GetPostUseCase(postRepository::getPost)
    }

    @Provides
    @Singleton
    fun provideAddPostUseCase(postRepository: PostRepository): AddPostUseCase {
        return AddPostUseCase(postRepository::addPost)
    }

    @Provides
    @Singleton
    fun provideRemovePostUseCase(postRepository: PostRepository): RemovePostUseCase {
        return RemovePostUseCase(postRepository::removePost)
    }

    @Provides
    @Singleton
    fun updatePostUseCase(postRepository: PostRepository): UpdatePostUseCase {
        return UpdatePostUseCase(postRepository::updatePost)
    }

    @Provides
    @Singleton
    fun updatePostCountUseCase(postRepository: PostRepository): UpdatePostCountUseCase {
        return UpdatePostCountUseCase(postRepository::updateReadCount)
    }

}