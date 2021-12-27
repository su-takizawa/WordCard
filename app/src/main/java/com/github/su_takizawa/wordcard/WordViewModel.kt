package com.github.su_takizawa.wordcard

import androidx.lifecycle.*
import com.github.su_takizawa.wordcard.module.Folder
import com.github.su_takizawa.wordcard.module.Word
import kotlinx.coroutines.launch

class WordViewModel(private val repository: WordRepository) : ViewModel() {

    // LiveDataを使用し、allWordsが返すものをキャッシュすると、いくつかの利点があります。
    // - オブザーバーにデータを配置（変更をポーリングする代わりに）し、データが実際に変更されたときにUIは更新するだけで済みます
    // - リポジトリは、ViewModelを介してUIから完全に分離されています。
    val allWords: LiveData<List<Word>> = repository.allWords.asLiveData()
    val allFolders: LiveData<List<Folder>> = repository.allFolders.asLiveData()

    fun getWords(folderId: Int): LiveData<List<Word>> = repository.getWords(folderId).asLiveData()

    /**
     * 新しいコルーチンを起動して、データをブロックしない方法で挿入します
     */
    fun folderInsert(folder: Folder) = viewModelScope.launch {
        repository.folderInsert(folder)
    }

    fun folderUpdate(folder: Folder) = viewModelScope.launch {
        repository.folderUpdate(folder)
    }

    fun folderDelete(folder: Folder) = viewModelScope.launch {
        repository.folderDelete(folder)
    }

    fun wordInsert(word: Word) = viewModelScope.launch {
        repository.wordInsert(word)
    }

    fun wordUpdate(word: Word) = viewModelScope.launch {
        repository.wordUpdate(word)
    }

    fun wordDelete(word: Word) = viewModelScope.launch {
        repository.wordDelete(word)
    }
}

class WordViewModelFactory(private val repository: WordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}