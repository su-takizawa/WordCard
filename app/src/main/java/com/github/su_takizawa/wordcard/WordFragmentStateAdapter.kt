package com.github.su_takizawa.wordcard

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.github.su_takizawa.wordcard.module.Word
import com.google.gson.Gson


class WordFragmentStateAdapter(
    initial: List<Word>,
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    private val list = mutableListOf<Word>()

    init {
        list.addAll(initial)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        val wordFragment = WordFragment.newInstance(Gson().toJson(list[position]))
        val myObserver = MyObserver(wordFragment)
        this.registerAdapterDataObserver(myObserver)
        return wordFragment
    }

    override fun getItemId(position: Int): Long {
        return list[position].id.toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return list.any { it.id.toLong() == itemId }
    }

    fun updateList(newList: List<Word>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return list.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return list[oldItemPosition] == newList[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return list[oldItemPosition] == newList[newItemPosition]
            }
        })

        list.clear()
        list.addAll(newList)

        diff.dispatchUpdatesTo(this)
    }

    inner class MyObserver(wordFragment: WordFragment) : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            Log.d("TAG", "positionStart:$positionStart")

        }

        override fun onChanged() {
            super.onChanged()
            Log.d("TAG", "AdapterDataObserver_onChange")
        }

    }

}