package com.example.scheduleapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.scheduleapp.model.ScheInfo

@Dao
interface ScheDao {
    @Insert
    fun insertScheData(ScheInfo : ScheInfo)

    @Update
    fun updateScheData(ScheInfo: ScheInfo)

    @Delete
    fun deleteScheData(ScheInfo: ScheInfo)

    @Query("SELECT * FROM SCHEINFO ORDER BY ScheDate")
    fun getAllReadDate() : List<ScheInfo>
}