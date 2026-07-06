package com.example.mvvmclean.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    /** Reactive stream of every note; Room re-emits on any table change. */
    @Query("SELECT * FROM notes")
    fun observeNotes(): Flow<List<NoteEntity>>

    /** Inserts a single note and returns its generated row id. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity): Long

    /** Bulk insert used when seeding the cache from the remote API. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(notes: List<NoteEntity>)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM notes")
    suspend fun count(): Int
}
