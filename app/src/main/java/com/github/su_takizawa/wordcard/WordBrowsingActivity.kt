package com.github.su_takizawa.wordcard

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.ToggleButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.github.su_takizawa.wordcard.module.Word
import java.lang.Math.abs
import java.util.*


class WordBrowsingActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory((application as WordsApplication).repository)
    }

    private lateinit var tts: TextToSpeech

    private var folderId: Int = 0

    private lateinit var viewpager: ViewPager2

    private lateinit var seekBar: SeekBar

    private var wordList: List<Word> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_browsing)

        folderId = intent.getStringExtra("FOLDER_ID")!!.toInt();

        // ViewPager2のインスタンス化
        viewpager = findViewById(R.id.viewpager)
        // ページインスタンスを用意
        val wordListAdapter = WordFragmentListAdapter(wordList, this)
        // セット
        viewpager.adapter = wordListAdapter

        //wordリストの取得
        wordViewModel.getWords(folderId).observe(this, { words ->
            wordListAdapter.updateList(words)
        })

        // カルーセルの動きをつける用
        viewpager.offscreenPageLimit = 2// これは左右のアイテムを描画するために必要
        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.page_margin)
        val offsetPx = resources.getDimensionPixelOffset(R.dimen.offset)
        viewpager.setPageTransformer { page, position ->
            val offset = position * (2 * offsetPx + pageMarginPx)
            page.translationX = -offset
        }
        viewpager.setPageTransformer(CompositePageTransformer().apply {
            addTransformer { page, position ->
                val offset = position * (2 * offsetPx + pageMarginPx)
                page.translationX = -offset
            }

            addTransformer { page, position ->
                val scale = 1 - (abs(position) / 6)
                page.scaleX = scale
                page.scaleY = scale
            }
        })

        val btPlay = findViewById<Button>(R.id.a05BtPlay)
        btPlay.setOnClickListener {
            val fragments = supportFragmentManager.fragments
            val fragment = fragments[viewpager.currentItem] as WordFragment
            fragment.view?.findViewById<TextView>(R.id.a05TvItem)?.let { tvItem ->
                val lang = when (fragment.isRear) {
                    false -> fragment.word.frontLang
                    true -> fragment.word.rearLang
                }
                speechText(lang, tvItem.text.toString())
                //tvItem.performClick()
            }
        }

        val tgAuto = findViewById<ToggleButton>(R.id.a05TbAuto)
        tgAuto.isChe
        tgAuto.setOnCheckedChangeListener { _, isChecked ->
            val fragments = supportFragmentManager.fragments
            val fragment = fragments[viewpager.currentItem] as WordFragment
            if (isChecked) {
                fragment.view?.findViewById<TextView>(R.id.a05TvItem)?.let { tvItem ->
                    val lang = when (fragment.isRear) {
                        false -> fragment.word.frontLang
                        true -> fragment.word.rearLang
                    }
                    speechText(lang, tvItem.text.toString())
                }
            }
        }

        seekBar = findViewById(R.id.a05Sb)
//        sb.progress
//        sb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener(){
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                tts
//            }
//
//        })

        tts = TextToSpeech(this, this)
        /*
        - 再生ボタンの実装

        Fragmentのメインのテキストに書かれているテキストを再生
        メインlangも参照

        再生中はポーズボタンになる

        再生速度も調整可能

        - Autoボタンの実装


        メインのテキストを再生したら
        FragmentにあるTextViewのOnClickLisnerを外部から動かして
        反転したらそのテキストを再生
        最後のFragmentになったら停止
        次のFragmentへ遷移


         */
    }

    /**
     * アクションバーにつけるメニュー作成
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * アクションバーにつけるメニューを選択時
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.a05BtList -> {
                val intent = Intent(this, WordListActivity::class.java)
                intent.putExtra("FOLDER_ID", folderId.toString())
                startActivity(intent)
            }
        }
        return true
    }

    override fun onInit(status: Int) {

        val textToSpeechInitListener =
            TextToSpeech.OnInitListener { status ->
                // TTS初期化
                if (TextToSpeech.SUCCESS == status) {
                    Log.d(ContentValues.TAG, "initialized")
                } else {
                    Log.e(ContentValues.TAG, "failed to initialize")
                }
            }
    }


    private fun shutDown() {
        // to release the resource of TextToSpeech
        tts.shutdown()
    }

    private fun speechText(lang: String, text: String) {
        val result = tts.setLanguage(Locale(lang))
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            //言語データがダウンロードされていません、Wifi環境へ繋ぐかもしくは設定画面よりダウンロードしてください。
            //お使いの端末ではこの言語はサポートされていません。
            Log.e("Text2Speech", "$result is not supported")
        }
        Log.e("Text2Speech", "$result debug")
        if (text.isNotEmpty()) {
            if (tts.isSpeaking) {
                Log.v("Text2Speech", "STOP")
                tts.stop()
                return
            }
            setSpeechRate()
            setSpeechPitch()
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "messageID")
            setTtsListener()
        }
    }

    // 読み上げのスピード
    private fun setSpeechRate() {
        tts.setSpeechRate(seekBar.progress / 10.toFloat())
    }

    // 読み上げのピッチ
    private fun setSpeechPitch() {
        tts.setPitch(1.0.toFloat())
    }

    // 読み上げの始まりと終わりを取得
    private fun setTtsListener() {
        val listenerResult =
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onDone(utteranceId: String) {
                    Log.d("TAG", "progress on Done $utteranceId")
                }

                override fun onError(utteranceId: String) {
                    Log.d("TAG", "progress on Error $utteranceId")
                }

                override fun onStart(utteranceId: String) {
                    Log.d("TAG", "progress on Start $utteranceId")
                }
            })
        if (listenerResult != TextToSpeech.SUCCESS) {
            Log.e("TAG", "failed to add utterance progress listener")
        }
    }
}