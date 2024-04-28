package com.dokja.mizumi.di

import android.app.Application
import android.content.Context
import com.dokja.mizumi.AppPreferences
import com.dokja.mizumi.data.local.AppDatabaseOperations
import com.dokja.mizumi.data.local.MizumiDatabase
import com.dokja.mizumi.data.local.chapter.ChapterBodyDao
import com.dokja.mizumi.data.local.chapter.ChapterDao
import com.dokja.mizumi.data.local.library.LibraryDao
import com.dokja.mizumi.data.manager.LocalUserManagerImpl
import com.dokja.mizumi.data.network.Constants
import com.dokja.mizumi.data.network.MizuListApi
import com.dokja.mizumi.domain.manager.LocalUserManager
import com.dokja.mizumi.domain.usecases.AppEntryUseCases
import com.dokja.mizumi.domain.usecases.ReadAppEntry
import com.dokja.mizumi.domain.usecases.SaveAppEntry
import com.dokja.mizumi.presentation.reader.manager.ReaderManager
import com.dokja.mizumi.repository.AppFileResolver
import com.dokja.mizumi.repository.AppRepository
import com.dokja.mizumi.repository.BookChaptersRepository
import com.dokja.mizumi.repository.ChapterBodyRepository
import com.dokja.mizumi.repository.LibraryBookRepository
import com.dokja.mizumi.utils.NotificationsCenter

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    val mainDatabaseName = "bookEntry"

    @Provides
    fun provideAppContext(@ApplicationContext context: Context) = context

    @Provides
    @Singleton
    fun provideAppPreferences(@ApplicationContext context: Context): AppPreferences {
        return AppPreferences(context)
    }

    @Provides
    @Singleton
    fun provideRepository(
        database: MizumiDatabase,
        @ApplicationContext context: Context,
        libraryBooksRepository: LibraryBookRepository,
        bookChaptersRepository: BookChaptersRepository,
        chapterBodyRepository: ChapterBodyRepository,
        appFileResolver: AppFileResolver,
    ): AppRepository {
        return AppRepository(
            database,
            context,
            mainDatabaseName,
            libraryBooksRepository,
            bookChaptersRepository,
            chapterBodyRepository,
            appFileResolver
        )
    }

    @Provides
    @Singleton
    fun provideMizuListApi(): MizuListApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(MizuListApi::class.java)
    }

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

    @Provides
    @Singleton
    fun provideChapterDao(database: MizumiDatabase): ChapterDao = database.getChapterDao()

    @Provides
    @Singleton
    fun provideChapterBodyDao(database: MizumiDatabase): ChapterBodyDao = database.getChapterBody()

    @Provides
    @Singleton
    fun provideLibraryBooksRepository(
        libraryDao: LibraryDao,
        operations: AppDatabaseOperations,
        @ApplicationContext context: Context,
        appFileResolver: AppFileResolver,
        appCoroutineScope: AppCoroutineScope,
    ): LibraryBookRepository = LibraryBookRepository(
        libraryDao, operations, context, appFileResolver, appCoroutineScope,
    )

    @Provides
    @Singleton
    fun provideAppCoroutineScope(): AppCoroutineScope {
        return object : AppCoroutineScope {
            override val coroutineContext =
                SupervisorJob() + Dispatchers.Main.immediate + CoroutineName("App")
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabaseOperations(database: MizumiDatabase): AppDatabaseOperations {
        return database
    }

    @Provides
    @Singleton
    fun provideAppFileResolver(@ApplicationContext context: Context): AppFileResolver {
        return AppFileResolver(context = context)
    }

    @Provides
    @Singleton
    fun provideChapterBooksRepository(
        chapterDao: ChapterDao,
        databaseOperations: AppDatabaseOperations
    ): BookChaptersRepository {
        return BookChaptersRepository(chapterDao, databaseOperations)
    }

    @Provides
    @Singleton
    fun provideChapterBodyRepository(
        database: MizumiDatabase,
        bookChaptersRepository: BookChaptersRepository,
    ): ChapterBodyRepository {
        return ChapterBodyRepository(
            chapterBodyDao = database.getChapterBody(),
            bookChaptersRepository = bookChaptersRepository,
            operations = database
        )
    }

    @Provides
    @Singleton
    fun provideNotificationCenter(
        @ApplicationContext context: Context,
    ): NotificationsCenter {
        return NotificationsCenter(context)
    }

    @Provides
    @Singleton
    fun provideReaderManager(
        appRepository: AppRepository,
        appCoroutineScope: AppCoroutineScope,
        @ApplicationContext context: Context,
        localUserManager: LocalUserManager
    ): ReaderManager {
        return ReaderManager(
            appRepository,
            context,
            appScope = appCoroutineScope,
            localUserManager = localUserManager
        )
    }
}