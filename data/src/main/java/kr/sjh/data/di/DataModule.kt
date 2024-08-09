package kr.sjh.data.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kr.sjh.data.repository.AuthRepositoryImpl
import kr.sjh.data.repository.ChatRepositoryImpl
import kr.sjh.data.repository.PostRepositoryImpl
import kr.sjh.data.repository.UserRepositoryImpl
import kr.sjh.data.repository.preferences.DataStoreRepositoryImpl
import kr.sjh.data.utils.FileUtil
import kr.sjh.domain.repository.firebase.AuthRepository
import kr.sjh.domain.repository.firebase.ChatRepository
import kr.sjh.domain.repository.firebase.PostRepository
import kr.sjh.domain.repository.firebase.UserRepository
import kr.sjh.domain.repository.preferences.DataStoreRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Provides
    @Singleton
    fun provideFirebaseFireStore() = Firebase.firestore
        .apply {
            firestoreSettings =
                FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()
        }

    @Provides
    @Singleton
    fun provideFirebaseFireAuth() = Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseStorage() = Firebase.storage

    @Provides
    @Singleton
    fun provideFileUtil() = FileUtil

    @Provides
    @Singleton
    fun provideBoardRepository(
        storage: FirebaseStorage,
        db: FirebaseFirestore,
        auth: FirebaseAuth,
    ): PostRepository {
        return PostRepositoryImpl(
            storage,
            db,
            auth,
        )
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth, fireStore: FirebaseFirestore
    ): AuthRepository {
        return AuthRepositoryImpl(
            auth,
            fireStore,
        )
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        fireStore: FirebaseFirestore,
        auth: FirebaseAuth,
        storage: FirebaseStorage,
        fileUtil: FileUtil,
        @ApplicationContext context: Context
    ): UserRepository {
        return UserRepositoryImpl(
            fireStore, auth, storage, fileUtil, context
        )
    }

    @Provides
    @Singleton
    fun provideDataStoreRepository(
        @ApplicationContext applicationContext: Context
    ): DataStoreRepository {
        return DataStoreRepositoryImpl(
            applicationContext
        )
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        fireStore: FirebaseFirestore, fireAuth: FirebaseAuth
    ): ChatRepository {
        return ChatRepositoryImpl(
            fireStore, fireAuth
        )
    }
}