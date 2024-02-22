package com.dokja.mizumi.di

import android.app.Application
import android.content.Context
import com.dokja.mizumi.data.local.MizumiDatabase
import com.dokja.mizumi.data.manager.LocalUserManagerImpl
import com.dokja.mizumi.domain.manager.LocalUserManager
import com.dokja.mizumi.domain.usecases.AppEntryUseCases
import com.dokja.mizumi.domain.usecases.ReadAppEntry
import com.dokja.mizumi.domain.usecases.SaveAppEntry
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideAppContext(@ApplicationContext context: Context) = context

    @Singleton
    @Provides
    fun provideMizumiDatabase(@ApplicationContext context: Context) =
        MizumiDatabase.getInstance(context)

    @Provides
    fun provideLibraryDao(mizumiDatabase: MizumiDatabase) = mizumiDatabase.getLibraryDao()


    @Provides
    @Singleton
    fun provideLocalUserManager(
        application: Application
    ): LocalUserManager = LocalUserManagerImpl(context = application)

    @Provides
    @Singleton
    fun provideAppEntryUseCases(
        localUserManager: LocalUserManager
    ): AppEntryUseCases = AppEntryUseCases(
        readAppEntry = ReadAppEntry(localUserManager),
        saveAppEntry = SaveAppEntry(localUserManager)
    )
}