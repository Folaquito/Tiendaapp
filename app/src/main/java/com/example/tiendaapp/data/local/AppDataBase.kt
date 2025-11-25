package com.example.tiendaapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tiendaapp.model.JuegoEntity
import com.example.tiendaapp.data.local.JuegoDao

@Database(
    entities = [JuegoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun juegoDao(): JuegoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "games_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}