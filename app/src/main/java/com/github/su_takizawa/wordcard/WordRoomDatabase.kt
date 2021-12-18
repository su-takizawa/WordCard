package com.github.su_takizawa.wordcard

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.su_takizawa.wordcard.module.Folder
import com.github.su_takizawa.wordcard.module.FolderDao
import com.github.su_takizawa.wordcard.module.Word
import com.github.su_takizawa.wordcard.module.WordDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

// Annotates class to be a Room Database with a table (entity) of the Word class
/**
 * Wordルームデーターベース
 * FolderクラスとWordクラスとのテーブル（エンティティ）を使用してルームデータベースになるようにクラスにアノテーションをつける
 */
@Database(entities = arrayOf(Folder::class, Word::class), version = 1, exportSchema = false)
public abstract class WordRoomDatabase : RoomDatabase() {

    abstract fun folderDao(): FolderDao
    abstract fun wordDao(): WordDao

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.folderDao(), database.wordDao())
                }
            }
        }

        suspend fun populateDatabase(folderDao: FolderDao, wordDao: WordDao) {
            // Delete all content here.
            wordDao.deleteAll()
            wordDao.deleteAll()
            val newFolder = Folder(0, "ベトナム語")
            folderDao.insert(newFolder)
            Log.v("TAG", "after insert ${folderDao.getAll().toString()}")
            val newFolder2 = Folder(0, "中国語")
            folderDao.insert(newFolder2)
            Log.v("TAG", "after insert ${folderDao.getAll().toString()}")
            val folderList = folderDao.getAll().flattenToList()
            val updateTaget =
                folderList.find { it.id == getFolderId(folderList, newFolder.folderName) }
            updateTaget?.let {
                it.folderName = "ベトナム語中級"
                folderDao.update(it)
            }
            Log.v("TAG", "after update ${folderDao.getAll().toString()}")
            val folderList2 = folderDao.getAll().flattenToList()
            val folderId = getFolderId(folderList2, updateTaget?.folderName ?: "")
            Log.v("TAG", "folderId:$folderId")
            val newWord = Word(0, folderId, "vn", "xin chao", "ja", "こんにちは")
            wordDao.insert(newWord)
            folderDao.loadFolderAndWords().forEach {
                Log.v("TAG", "after word insert ${it.folder}:${it.words}}")
            }
        }

        fun getFolderId(folders: List<Folder>, key: String): Int {
            return folders.find { it.folderName == key }?.id ?: -1;
        }

        suspend fun <T> Flow<List<T>>.flattenToList() = flatMapConcat { it.asFlow() }.toList()
    }

    companion object {
        //シングルトンは、データベースの複数のインスタンスが同時に開くのを防ぎます。
        @Volatile
        private var INSTANCE: WordRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): WordRoomDatabase {
            // INSTANCEがnullでない場合は、それを返します。
            // そうである場合は、データベースを作成します
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordRoomDatabase::class.java,
                    "word_database"
                ).addCallback(WordDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

