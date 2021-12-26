package com.github.su_takizawa.wordcard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.su_takizawa.wordcard.module.Folder
import com.github.su_takizawa.wordcard.module.Util.Companion.getSpinnerToLang
import com.github.su_takizawa.wordcard.module.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.toList

class MainActivity : AppCompatActivity() {

    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory((application as WordsApplication).repository)
    }


    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            if (result?.resultCode == Activity.RESULT_OK) {
                Log.v("TAG", "resultOk")
            } else {
                Log.v("TAG", "resultNg")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //　ボタンを設定
        val btnDbInit = findViewById<Button>(R.id.btnDbInit)

        btnDbInit.setOnClickListener {
            (0..7).forEach {
                val lang = getSpinnerToLang(it)
                Log.v("TAG", lang)
            }
            val newFolder = Folder(0, "ベトナム語")
            wordViewModel.folderInsert(newFolder)
            val newFolder2 = Folder(0, "中国語")
            wordViewModel.folderInsert(newFolder2)
            var folderId = 0
            wordViewModel.allFolders.observe(this, { folders ->
                folderId = getFolderId(folders, newFolder.folderName)
                Log.v("TAG", "folderIdObserve:$folderId")
                val newWord = Word(0, folderId, "vn", "xin chao", "ja", "こんにちは")
                wordViewModel.wordInsert(newWord)
            })
        }

        //　ボタンを設定
        val button = findViewById<Button>(R.id.btnFolderList)

        button.setOnClickListener {
            Log.v("TAG", "btnFolderList")

            val intent = Intent(this@MainActivity, FolderListActivity::class.java)
            //@Deprecated
//            startActivityForResult(intent, newWordActivityRequestCode)
            startForResult.launch(intent)
        }
    }

    fun getFolderId(folders: List<Folder>, key: String): Int {
        return folders.find { it.folderName == key }?.id ?: -1;
    }

    suspend fun <T> Flow<List<T>>.flattenToList() = flatMapConcat { it.asFlow() }.toList()
}