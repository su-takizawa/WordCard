package com.github.su_takizawa.wordcard.module

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "word_table",
    foreignKeys = arrayOf(
        ForeignKey(//外部キー設定
            entity = Folder::class,//親クラス
            parentColumns = arrayOf("id"),//親を参照するためのフィールド
            childColumns = arrayOf("folderId"),//parentColumnsに設定したフィールドと同じ値を持つ、子のフィールド
            onDelete = ForeignKey.CASCADE//親を削除するときに子も削除する
        )
    )
)
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val folderId: Int,
    val frontLang: String,
    val frontWord: String,
    val rearLang: String,
    val rearWord: String,
)
