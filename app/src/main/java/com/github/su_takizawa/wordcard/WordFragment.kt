package com.github.su_takizawa.wordcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.su_takizawa.wordcard.module.Word
import com.google.gson.Gson

private const val ARG_PARAM1 = "param1"

/**
 * これは簡単な [Fragment] のサブクラス
 * 使用するには [WordFragment.newInstance] のファクトリクラスを
 * 作成するとインスタンスが取得できFragmentになる
 */
class WordFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var word: Word
    var isRear = false
    /*
    表Lang
    表Word
    裏Lang
    裏Word
    メインLang
    メインWord
    isRear

    メインWordのOnClicイベントを押されたときに
    isRearを反転させて
    　trueのときは裏をメインに設定
     falseのときは表をメインに設定


     */

    companion object {
        /**
         * このファクトリメソッドを使用して、
         * 提供されたパラメーターを使用するこのフラグメントの新しいインスタンスを作成します。
         *
         * @param param1 Parameter 1.
         * @return フラグメントWordFragmentの新しいインスタンス。
         */
        @JvmStatic
        fun newInstance(param1: String) =
            WordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            word = Gson().fromJson(it.getString(ARG_PARAM1), Word::class.java) as Word
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_word, container, false)
    }

    /**
     * OnCreateの次に呼ばれるクラス
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvItem = view.findViewById<TextView>(R.id.a05TvItem)
        if (!isRear) {
            tvItem.text = word.frontWord
        } else {
            tvItem.text = word.rearWord
        }
        tvItem.setOnClickListener {
            isRear = !isRear
            if (!isRear) {
                tvItem.text = word.frontWord
            } else {
                tvItem.text = word.rearWord
            }
        }
    }

}