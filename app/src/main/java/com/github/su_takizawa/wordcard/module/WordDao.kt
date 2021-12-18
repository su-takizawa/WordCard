package com.github.su_takizawa.wordcard.module

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Insert
    suspend fun insert(word: Word)

    @Update
    suspend fun update(word: Word)

    @Delete
    suspend fun delete(word: Word)

    @Query("delete from word_table")
    suspend fun deleteAll()

    @Query("select * from word_table order by id")
    fun getAll(): Flow<List<Word>>

    @Query("select * from word_table where folderId = :folderId order by id")
    fun getWords(folderId: Int): Flow<List<Word>>

    @Query("select * from word_table where id = :id")
    suspend fun getWord(id: Int): Word
}