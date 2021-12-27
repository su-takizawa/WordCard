package com.github.su_takizawa.wordcard.module

import androidx.room.Embedded
import androidx.room.Relation

class FolderAndWords {
    @Embedded
    lateinit var folder: Folder

    @Relation(parentColumn = "id", entityColumn = "folderId")
    lateinit var words: List<Word>

}
