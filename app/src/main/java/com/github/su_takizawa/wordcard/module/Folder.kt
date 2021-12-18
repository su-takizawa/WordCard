package com.github.su_takizawa.wordcard.module

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folder_table")
data class Folder(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var folderName: String
)
