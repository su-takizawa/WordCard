package com.github.su_takizawa.wordcard.module

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    //suspendはKotlinコルーチンの一時停止機能を利用する装飾子
    @Insert
    suspend fun insert(folder: Folder)

    @Update
    suspend fun update(folder: Folder)

    @Delete
    suspend fun delete(folder: Folder)

    @Query("delete from folder_table")
    suspend fun deleteAll()

    @Query("select * from folder_table order by id")
    fun getAll(): Flow<List<Folder>>

    @Query("select * from folder_table where id = :id")
    suspend fun getFolder(id: Int): Folder

    @Transaction
    @Query("select * from folder_table")
    suspend fun loadFolderAndWords(): List<FolderAndWords>
}