package com.github.su_takizawa.wordcard

import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.su_takizawa.wordcard.module.Folder
import com.github.su_takizawa.wordcard.module.Util.Companion.gsonResDecode
import com.google.gson.Gson

class FolderListActivity : ListBaseActivity<FolderListActivity, Folder, FolderEditActivity>() {

    override fun resultOkProcess(result: ActivityResult) {

        result.data?.getStringExtra(FolderEditActivity.EXTRA_REPLY)?.let { reply ->
            Log.v("TAG", "reply:${reply}")
            val replyDecode = gsonResDecode(reply, Folder::class.java)
            if (replyDecode.mode == EditBaseActivity.Mode.ADD) {
                wordViewModel.folderInsert(replyDecode.item)
            } else {
                wordViewModel.folderUpdate(replyDecode.item)
            }
        }
    }

    override fun getActivity(): Int = R.layout.activity_folder_list

    override fun getRecyclerView(): Int = R.id.a01Recyclerview

    override fun initRecyclerView() {
        val wordListAdapter = FolderListAdapter()
        adapter = wordListAdapter as ListAdapter<Folder, RecyclerView.ViewHolder>
        recyclerView.adapter = wordListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // allFoldersによって返されるLiveDataにオブザーバーを追加する。
        // onChanged()メソッドは、監視されたデータが変更され、アクティビティがフォアグラウンドにあるときに起動する。
        wordViewModel.allFolders.observe(this, { folders ->
            // アダプタ内のWordのキャッシュされたコピーを更新します。
            folders.let { adapter.submitList(it) }
        })
    }

    override fun getRecycleRb(): Int = R.id.a01RbItem

    override fun getRecycleTv(): Int = R.id.a01TvItem

    override fun delete(item: Folder) {
        wordViewModel.folderDelete(item)
    }

    override fun getEditClass(): Class<FolderEditActivity> = FolderEditActivity::class.java

    override fun getEditRequest(item: Folder): String {
        val gson = Gson().toJson(item)
        Log.v("TAG", "GSON:$gson")
        return gson
    }

    override fun getAddRequest(): String {
        val gson = Gson().toJson(Folder(0, ""))
        Log.v("TAG", "GSON:$gson")
        return gson
    }

    override fun getFabAdd(): Int = R.id.a01FabAdd

    override fun getFabEdit(): Int = R.id.a01FabEdit


}
