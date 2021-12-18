package com.github.su_takizawa.wordcard

import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.su_takizawa.wordcard.module.Util
import com.github.su_takizawa.wordcard.module.Word
import com.google.gson.Gson

class WordListActivity : ListBaseActivity<WordListActivity, Word, WordEditActivity>() {

    private var folderId: Int = 0

    override fun resultOkProcess(result: ActivityResult) {
        result.data?.getStringExtra(WordEditActivity.EXTRA_REPLY)?.let { reply ->
            Log.v("TAG", "reply:${reply}")
            val replyDecode = Util.gsonResDecode(reply, Word::class.java)
            if (replyDecode.mode == EditBaseActivity.Mode.ADD) {
                wordViewModel.wordInsert(replyDecode.item)
            } else {
                wordViewModel.wordUpdate(replyDecode.item)
            }
        }
    }

    override fun getActivity(): Int = R.layout.activity_word_list

    override fun getRecyclerView(): Int = R.id.a03Recyclerview

    override fun initRecyclerView() {
        val wordListAdapter = WordListAdapter()
        adapter = wordListAdapter as ListAdapter<Word, RecyclerView.ViewHolder>
        recyclerView.adapter = wordListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)




        folderId = intent.getStringExtra("FOLDER_ID")!!.toInt();
        // getWords()によって返されるLiveDataにオブザーバーを追加する。
        // onChanged()メソッドは、監視されたデータが変更され、アクティビティがフォアグラウンドにあるときに起動する。
        wordViewModel.getWords(folderId).observe(this, { words ->
            // アダプタ内のWordのキャッシュされたコピーを更新します。
            words.let {
                adapter.submitList(it)
                Log.v("TAG", it.toString())
            }
        })
    }

    override fun getRecycleRb(): Int = R.id.a03RbItem

    override fun getRecycleTv(): Int = R.id.a03TvItem

    override fun delete(item: Word) {
        wordViewModel.wordDelete(item)
    }

    override fun getEditClass(): Class<WordEditActivity> = WordEditActivity::class.java

    override fun getEditRequest(item: Word): String {
        val gson = Gson().toJson(item)
        Log.v("TAG", "GSON:$gson")
        return gson
    }

    override fun getAddRequest(): String {
        val gson = Gson().toJson(Word(0, folderId, "", "", "", ""))
        Log.v("TAG", "GSON:$gson")
        return gson
    }

    override fun getFabAdd(): Int = R.id.a03FabAdd

    override fun getFabEdit(): Int = R.id.a03FabEdit
}