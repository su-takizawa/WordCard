package com.github.su_takizawa.wordcard

import android.content.ContentValues.TAG
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.su_takizawa.wordcard.module.Word
import java.util.*


class WordListAdapter : ListAdapter<Word, WordListAdapter.WordViewHolder>(WordsComparator()) {

    private var checkPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {

        return WordViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.frontLang, current.frontWord, current.rearWord, current.id)
        val radioButton = holder.itemView.findViewById<RadioButton>(R.id.a03RbItem)
        radioButton.isChecked = position == checkPosition
        radioButton.setOnClickListener {
            checkPosition = position
            notifyDataSetChanged()
        }

    }

    class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val wordLangTv: TextView = itemView.findViewById(R.id.a03TvLang)
        private val wordItemView: TextView = itemView.findViewById(R.id.a03TvItem)
        private val radioButton: RadioButton = itemView.findViewById(R.id.a03RbItem)

        fun bind(text1: String, text2: String, text3: String, id: Int) {
            wordLangTv.text = text1
            wordItemView.text = "${text2}/${text3}"
            radioButton.text = id.toString()

        }

        companion object {
            private lateinit var tts: TextToSpeech

            fun create(parent: ViewGroup): WordViewHolder {
                //レイアウトXMLからViewを生成
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.a03_recyclerview_item, parent, false)


                val textToSpeechInitListener =
                    TextToSpeech.OnInitListener { status ->
                        // TTS初期化
                        if (TextToSpeech.SUCCESS == status) {
                            Log.d(TAG, "initialized")
                        } else {
                            Log.e(TAG, "failed to initialize")
                        }
                    }

                tts = TextToSpeech(parent.context, textToSpeechInitListener)

                //クリックイベントを登録
                view.setOnClickListener {
                    val wordLangTv: TextView = view.findViewById(R.id.a03TvLang)
                    //言語設定
                    val result = tts.setLanguage(Locale(wordLangTv.text.toString()))
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        //言語データがダウンロードされていません、Wifi環境へ繋ぐかもしくは設定画面よりダウンロードしてください。
                        //お使いの端末では言語はサポートされていません。
                        Log.e("Text2Speech", "$result is not supported")
                    }
                    speechText(view)
                }

                return WordViewHolder(view)
            }

            private fun speechText(view: View) {
                val editor = view.findViewById<TextView>(R.id.a03TvItem)
                // EditTextからテキストを取得
                val string = editor.text.toString().split("/")[0]
                if (string.isNotEmpty()) {
                    if (tts.isSpeaking) {
                        tts.stop()
                        return
                    }
                    val status = tts.speak(string, TextToSpeech.QUEUE_FLUSH, null, "messageID")

                    Log.e(TAG, "sppeek:$status")
                }
            }
        }

    }

    class WordsComparator : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem.frontWord + oldItem.rearWord == newItem.frontWord + newItem.rearWord
        }
    }

}