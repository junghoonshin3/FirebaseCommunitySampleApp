package kr.sjh.auth.di

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.sjh.domain.firebase.FirebaseDataBaseManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FirebaseDataBaseModule {
    @Provides
    @Singleton
    fun provideFirebaseDataBase() = Firebase.database

    @Provides
    @Singleton
    fun provideFirebaseDataBaseManager(db: FirebaseDatabase) =
        kr.sjh.domain.firebase.FirebaseDataBaseManager(db)


}