package com.dokja.mizumi.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.dokja.mizumi.data.local.chapter.Chapter
import com.dokja.mizumi.data.local.chapter.ChapterBody
import com.dokja.mizumi.data.local.chapter.ChapterBodyDao
import com.dokja.mizumi.data.local.chapter.ChapterDao
import com.dokja.mizumi.data.local.library.LibraryDao
import com.dokja.mizumi.data.local.library.LibraryItem
import com.dokja.mizumi.data.local.tracker.Track
import com.dokja.mizumi.data.local.tracker.TrackerDao
import com.dokja.mizumi.utils.Constants


/**
 * Execute the whole database calls as an atomic operation
 */
interface AppDatabaseOperations {
    suspend fun <T> transaction(block: suspend () -> T): T
}

@Database(
    entities = [
        LibraryItem::class,
        Chapter::class,
        ChapterBody::class,
        Track::class
    ],
    version = 8,
    exportSchema = false
) abstract class MizumiDatabase : RoomDatabase(), AppDatabaseOperations {
    abstract fun getLibraryDao(): LibraryDao
    abstract fun getChapterDao(): ChapterDao

    abstract fun getChapterBody(): ChapterBodyDao
    abstract fun getTrackerDao(): TrackerDao


    override suspend fun <T> transaction(block: suspend () -> T): T = withTransaction(block)
    companion object {
        @Volatile
        private var INSTANCE: MizumiDatabase? = null
        fun getInstance(context: Context): MizumiDatabase {
            /*
            if the INSTANCE is not null, then return it,
            if it is, then create the database and save
            in instance variable then return it.
            */
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MizumiDatabase::class.java,
                    Constants.DATABASE_NAME
                ).fallbackToDestructiveMigration().build()

                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}



