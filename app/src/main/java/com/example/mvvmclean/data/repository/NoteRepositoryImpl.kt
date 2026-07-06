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
 * Offline-first: Room is the source of truth and the remote API only seeds
 * an empty database on first launch. Network failures are swallowed so the
 * app works fully offline.
 */
@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val dao: NoteDao,
    private val api: NoteApiService,
) : NoteRepository {

    override fun getNotes(): Flow<List<Note>> = flow {
        coroutineScope {
            // Seed concurrently so the first emission isn't blocked on the
            // network. Room re-emits when the seed lands.
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

    // Best-effort: if the device is offline the app just starts with an
    // empty list. A real app would replace this with a proper sync strategy.
    private suspend fun seedFromRemoteIfEmpty() {
        if (dao.count() > 0) return
        runCatching { api.getNotes() }
            .onSuccess { dtos -> dao.insertAll(dtos.map { it.toEntity() }) }
    }
}
