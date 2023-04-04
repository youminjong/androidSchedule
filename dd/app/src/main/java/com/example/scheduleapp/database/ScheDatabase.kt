package com.example.scheduleapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.scheduleapp.model.ScheInfo

@Database(entities = [ScheInfo::class], version = 1)
abstract class ScheDatabase : RoomDatabase() {
    abstract fun scheDao() : ScheDao

    companion object{
        private var instance: ScheDatabase? = null
        @Synchronized
        fun getInstance(context: Context) : ScheDatabase?{
            if(instance == null ){
                synchronized(ScheDatabase :: class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ScheDatabase::class.java,
                        "sche-database"
                    ).build()
                }
            }
            return instance
        }
    }
}