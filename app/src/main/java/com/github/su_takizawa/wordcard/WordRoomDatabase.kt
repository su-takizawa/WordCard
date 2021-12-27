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

        //データベース作成時に一度だけ呼ばれる
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.folderDao(), database.wordDao())
                }
            }
            Log.d("CreateRoomDatabase", "version:" + db.version)
        }

        //データベースを開くたびに呼ばれる
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            Log.d("OpenRoomDatabase", "version:" + db.version)
        }

        suspend fun populateDatabase(folderDao: FolderDao, wordDao: WordDao) {
            // Delete all content here.
        }
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
                ).createFromAsset("predbdata/word_database.db")//assetsフォルダ内のDBを事前読み込み
                    .addCallback(WordDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

