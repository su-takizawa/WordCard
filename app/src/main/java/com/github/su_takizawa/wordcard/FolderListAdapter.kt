package com.github.su_takizawa.wordcard

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.su_takizawa.wordcard.module.Folder


class FolderListAdapter :
    ListAdapter<Folder, FolderListAdapter.FolderViewHolder>(WordsComparator()) {

    private var checkPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {

        return FolderViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.folderName, current.id)
        val radioButton = holder.itemView.findViewById<RadioButton>(R.id.a01RbItem)
        radioButton.isChecked = position == checkPosition
        radioButton.setOnClickListener {
            checkPosition = position
            notifyDataSetChanged()
        }

    }

    class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val folderItemView: TextView = itemView.findViewById(R.id.a01TvItem)
        private val radioButton: RadioButton = itemView.findViewById(R.id.a01RbItem)

        fun bind(text: String, id: Int) {
            folderItemView.text = text
            radioButton.text = id.toString()

        }

        companion object {
            fun create(parent: ViewGroup): FolderViewHolder {
                //レイアウトXMLからViewを生成
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.a01_recyclerview_item, parent, false)

                //クリックイベントを登録
                view.setOnClickListener {
                    Toast.makeText(
                        it.context,
                        it.findViewById<RadioButton>(R.id.a01RbItem).text,
                        Toast.LENGTH_LONG
                    ).show()

                    val intent = Intent(it.context, WordBrowsingActivity::class.java)
                    intent.putExtra("FOLDER_ID", it.findViewById<RadioButton>(R.id.a01RbItem).text)
                    it.context.startActivity(intent)
                }

                return FolderViewHolder(view)
            }
        }
    }

    class WordsComparator : DiffUtil.ItemCallback<Folder>() {
        override fun areItemsTheSame(oldItem: Folder, newItem: Folder): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Folder, newItem: Folder): Boolean {
            return oldItem.folderName == newItem.folderName
        }
    }

}