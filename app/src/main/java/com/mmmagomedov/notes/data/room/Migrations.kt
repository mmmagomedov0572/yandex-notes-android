package com.mmmagomedov.notes.data.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE notes ADD COLUMN lastUpdatedAt INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS notes_new (
                uid TEXT NOT NULL,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                color INTEGER NOT NULL,
                priority TEXT NOT NULL,
                createdAt INTEGER NOT NULL,
                changedAt INTEGER NOT NULL,
                selfDestructAt INTEGER,
                PRIMARY KEY(uid)
            )
        """.trimIndent())

        db.execSQL("""
            INSERT INTO notes_new(uid, title, content, color, priority, createdAt, changedAt, selfDestructAt)
            SELECT uid, title, content, color, importance, createdAt, changedAt, selfDestructAt
            FROM notes
        """.trimIndent())

        db.execSQL("DROP TABLE notes")
        db.execSQL("ALTER TABLE notes_new RENAME TO notes")
    }
}
