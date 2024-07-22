package kr.sjh.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.sjh.domain.repository.firebase.ChatRepository
import kr.sjh.domain.usecase.chat.GetChatRoomsUseCase
import kr.sjh.domain.usecase.chat.GetInitialMessagesUseCase
import kr.sjh.domain.usecase.chat.GetNextMessagesUseCase
import kr.sjh.domain.usecase.chat.SendMessageUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatUseCaseModule {
    @Provides
    @Singleton
    fun provideGetInitialMessagesUseCase(chatRepository: ChatRepository): GetInitialMessagesUseCase {
        return GetInitialMessagesUseCase(chatRepository::getInitialMessages)
    }

    @Provides
    @Singleton
    fun provideSendMessageUseCase(chatRepository: ChatRepository): SendMessageUseCase {
        return SendMessageUseCase(chatRepository::sendMessage)
    }

    @Provides
    @Singleton
    fun provideGetNextMessagesUseCase(chatRepository: ChatRepository): GetNextMessagesUseCase {
        return GetNextMessagesUseCase(chatRepository::getNextMessages)
    }

    @Provides
    @Singleton
    fun provideGetChatRoomsUseCase(chatRepository: ChatRepository): GetChatRoomsUseCase {
        return GetChatRoomsUseCase(chatRepository::getChatRooms)
    }
}