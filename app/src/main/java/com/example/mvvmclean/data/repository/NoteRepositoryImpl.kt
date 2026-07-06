package com.example.mvvmclean.data.repository

import com.example.mvvmclean.data.local.NoteDao
import com.example.mvvmclean.data.local.NoteEntity
import com.example.mvvmclean.data.mapper.toDomain
import com.example.mvvmclean.data.mapper.toEntity
import com.example.mvvmclean.data.remote.NoteApiService
import com.example.mvvmclean.domain.model.Note
import com.example.mvvmclean.domain.repository.NoteRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Offline-first implementation of [NoteRepository].
 *
 * Room is the single source of truth: the UI always renders what is in the
 * database. The remote API is only used to seed an empty database on first
 * launch; a network failure is swallowed so the app works fully offline.
 */
@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val dao: NoteDao,
    private val api: NoteApiService,
) : NoteRepository {

    override fun getNotes(): Flow<List<Note>> = flow {
        coroutineScope {
            // Seed concurrently so the first database emission is never
            // blocked behind a network call; Room re-emits when it lands.
            launch { seedFromRemoteIfEmpty() }
            emitAll(
                dao.observeNotes()
                    .map { entities -> entities.map(NoteEntity::toDomain) },
            )
        }
    }

    override suspend fun createNote(title: String, content: String): Note {
        val now = System.currentTimeMillis()
        val entity = NoteEntity(
            title = title,
            content = content,
            createdAt = now,
            updatedAt = now,
        )
        val id = dao.insert(entity)
        return entity.copy(id = id).toDomain()
    }

    override suspend fun deleteNote(id: Long) {
        dao.deleteById(id)
    }

    /**
     * Seeds the local cache from the remote API the first time the app runs.
     * Best-effort by design: if the device is offline the app simply starts
     * with an empty list. A production app would replace this with a real
     * sync strategy (e.g. WorkManager + conflict resolution).
     */
    private suspend fun seedFromRemoteIfEmpty() {
        if (dao.count() > 0) return
        runCatching { api.getNotes() }
            .onSuccess { dtos -> dao.insertAll(dtos.map { it.toEntity() }) }
    }
}
