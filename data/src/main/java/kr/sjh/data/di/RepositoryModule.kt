package kr.sjh.data.di

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kr.sjh.data.repository.BoardRepositoryImpl
import kr.sjh.data.repository.LoginRepositoryImpl
import kr.sjh.data.repository.BoardRepository
import kr.sjh.data.repository.LoginRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun provideLoginRepository(
        @ApplicationContext context: Context,
        db: FirebaseDatabase,
        authApiClient: AuthApiClient,
        userApiClient: UserApiClient
    ): LoginRepository {
        return LoginRepositoryImpl(
            context,
            db,
            authApiClient,
            userApiClient
        )
    }

    @Provides
    @Singleton
    fun provideBoardRepository(
        storage: FirebaseStorage,
        db: FirebaseDatabase,
    ): BoardRepository {
        return BoardRepositoryImpl(
            storage,
            db
        )
    }
}