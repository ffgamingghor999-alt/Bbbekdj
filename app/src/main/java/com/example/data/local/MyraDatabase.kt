package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ActionLogDao
import com.example.data.local.dao.ConversationDao
import com.example.data.local.dao.MemoryNoteDao
import com.example.data.local.entity.ActionLogEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.MemoryNoteEntity

@Database(
    entities = [
        ConversationEntity::class,
        MemoryNoteEntity::class,
        ActionLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MyraDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun memoryNoteDao(): MemoryNoteDao
    abstract fun actionLogDao(): ActionLogDao

    companion object {
        @Volatile
        private var INSTANCE: MyraDatabase? = null

        fun getInstance(context: Context): MyraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyraDatabase::class.java,
                    "myra_ai_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
