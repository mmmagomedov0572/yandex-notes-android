package com.mmmagomedov.notes.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mmmagomedov.notes.data.model.NoteEntity

@Database(entities = [NoteEntity::class], version = 1, exportSchema = false)
abstract class RoomNotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: RoomNotesDatabase? = null

        fun getInstance(context: Context): RoomNotesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomNotesDatabase::class.java,
                    "notes_database"
                )
                    .addMigrations(
                         // MIGRATION_1_2, MIGRATION_2_3
                    )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}