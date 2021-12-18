package com.github.su_takizawa.wordcard

import androidx.annotation.WorkerThread
import com.github.su_takizawa.wordcard.module.Folder
import com.github.su_takizawa.wordcard.module.FolderDao
import com.github.su_takizawa.wordcard.module.Word
import com.github.su_takizawa.wordcard.module.WordDao
import kotlinx.coroutines.flow.Flow

// DAOをコンストラクターのプライベートプロパティとして宣言します。
// データベース全体ではなく、DAOにアクセスするだけでよいので
class WordRepository(private val folderDao: FolderDao, private val wordDao: WordDao) {

    // Roomは、すべてのクエリを別のスレッドで実行します。
    // Observed Flowは、データが変更されたときにオブザーバーに通知します。
    val allWords: Flow<List<Word>> = wordDao.getAll()
    val allFolders: Flow<List<Folder>> = folderDao.getAll()

    fun getWords(folderId: Int): Flow<List<Word>> = wordDao.getWords(folderId)

    // デフォルトでは、Roomはメインスレッドからサスペンドクエリを実行するため、以下の作業は必要はありません。
    // メインスレッドから切り離して、長時間実行されるデータベース作業を行わないようにします。
    @Suppress("RedundantSuspendModifier")
    @WorkerThread //ワーカスレッド以外で実行しようとしたら警告(メインスレッド等)
    suspend fun folderInsert(folder: Folder) {
        folderDao.insert(folder)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun folderUpdate(folder: Folder) {
        folderDao.update(folder)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun folderDelete(folder: Folder) {
        folderDao.delete(folder)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun wordInsert(word: Word) {
        wordDao.insert(word)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun wordUpdate(word: Word) {
        wordDao.update(word)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun wordDelete(word: Word) {
        wordDao.delete(word)
    }
}
