package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.MemoryNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryNoteDao {
    @Query("SELECT * FROM memory_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<MemoryNoteEntity>>

    @Query("SELECT * FROM memory_notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchNotes(query: String): Flow<List<MemoryNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: MemoryNoteEntity): Long

    @Query("DELETE FROM memory_notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    @Query("DELETE FROM memory_notes")
    suspend fun clearAllNotes()

    @Query("SELECT COUNT(*) FROM memory_notes")
    suspend fun getCount(): Int

    @Query("SELECT * FROM memory_notes ORDER BY timestamp DESC")
    suspend fun getAllNotesDirect(): List<MemoryNoteEntity>
}
