package com.nexters.mytine.data.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRAITONS: Array<Migration>
    get() = arrayOf(MIGRATION_1_2)

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS retrospect")
    }
}
