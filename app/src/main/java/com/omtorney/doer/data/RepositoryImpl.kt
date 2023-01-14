package com.omtorney.doer.data

import com.omtorney.doer.data.database.NoteDao
import com.omtorney.doer.domain.Repository
import com.omtorney.doer.model.Note
import com.omtorney.doer.util.Constants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val settingsStore: SettingsStore
) : Repository {
    override suspend fun addNote(note: Note) = noteDao.insert(note)

    override suspend fun updateNote(note: Note) = noteDao.update(note)

    override suspend fun deleteNote(note: Note) = noteDao.delete(note)

    override val getLineSeparatorState = settingsStore.getLineSeparatorState

    override suspend fun setLineSeparatorState(enabled: Boolean) =
        settingsStore.setLineSeparatorState(enabled)

    override val getAccentColor: Flow<Long> = settingsStore.getAccentColor

    override suspend fun setAccentColor(color: Long) = settingsStore.setAccentColor(color)

    override val getInitialColor = Constants.initialColor
}