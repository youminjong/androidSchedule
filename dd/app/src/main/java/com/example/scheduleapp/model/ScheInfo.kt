package com.example.scheduleapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ScheInfo {
    var ScheContent : String = ""
    var ScheDate : String = ""

    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}