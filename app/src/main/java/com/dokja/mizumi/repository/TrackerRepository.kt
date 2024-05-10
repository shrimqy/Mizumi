package com.dokja.mizumi.repository

import com.dokja.mizumi.data.local.tracker.Track
import com.dokja.mizumi.data.local.tracker.TrackerDao

class TrackerRepository(
    private val trackerDao: TrackerDao,
) {
    suspend fun insertTrack(track: Track) = trackerDao.insertTrack(track)

    suspend fun getTrack(libraryId: Int) = trackerDao.getTrackById(libraryId)

    fun getTrackByIdWithFlow(libraryId: Int) = trackerDao.getTrackByIdWithFlow(libraryId)

    suspend fun updateStatus(libraryId: Int, bookCategory: Int, chaptersRead: String?, rating: String?, startedDate: String?, completedAt: String?) = trackerDao.updateStatus(libraryId, bookCategory, chaptersRead, rating, completedAt, startedDate)

    suspend fun updateChapterRead(libraryId: Int, bookCategory: Int, chaptersRead: String?) = trackerDao.updateChapterRead(libraryId, bookCategory, chaptersRead)
}