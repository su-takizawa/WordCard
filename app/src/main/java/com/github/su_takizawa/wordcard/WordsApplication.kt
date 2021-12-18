package com.github.su_takizawa.wordcard

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class WordsApplication : Application() {
    // このスコープはプロセスによって破棄されるため、キャンセルする必要はありません。
    val applicationScope = CoroutineScope(SupervisorJob())

    // lazyを使用して、アプリケーションの起動時ではなく、データベースとリポジトリが必要なときのみ作成されるようにします
    val database by lazy { WordRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { WordRepository(database.folderDao(), database.wordDao()) }
}