package com.dokja.mizumi.data.local.tracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface TrackerDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: Track): Long

    @Query("SELECT * FROM Track WHERE Track.libraryId == :libraryId")
    suspend fun getTrackById(libraryId: Int): Track

    @Query("SELECT * FROM Track WHERE Track.libraryId == :libraryId")
    fun getTrackByIdWithFlow(libraryId: Int): Flow<Track>

    @Query("UPDATE Track SET bookCategory = :bookCategory, chaptersRead = :chaptersRead, rating = :rating, startedDate = :startedDate, completedAt = :completedAt WHERE libraryId == :libraryId ")
    suspend fun updateStatus(libraryId: Int, bookCategory: Int, chaptersRead: String?, rating: String?, startedDate: String?, completedAt: String?)

    @Query("UPDATE Track SET bookCategory = :bookCategory, chaptersRead = :chaptersRead WHERE libraryId == :libraryId ")
    suspend fun updateChapterRead(libraryId: Int, bookCategory: Int, chaptersRead: String?)
}