package com.mmmagomedov.notes.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mmmagomedov.notes.data.model.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    fun getNotesFlow(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE uid = :uid")
    suspend fun getNoteByUid(uid: String): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)

    @Query("DELETE FROM notes")
    suspend fun deleteAll()

    @Query("DELETE FROM notes WHERE uid = :uid")
    suspend fun deleteByUid(uid: String): Int
}
