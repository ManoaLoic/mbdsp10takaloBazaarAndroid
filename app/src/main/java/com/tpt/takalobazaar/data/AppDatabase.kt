package com.tpt.takalobazaar.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tpt.takalobazaar.models.Draft

@Database(entities = [Draft::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun draftDao(): DraftDao
}
