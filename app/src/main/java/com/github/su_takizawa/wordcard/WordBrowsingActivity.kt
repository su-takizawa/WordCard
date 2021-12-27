package com.github.su_takizawa.wordcard

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.SeekBar
import android.widget.ToggleButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.github.su_takizawa.wordcard.module.Word
import com.github.su_takizawa.wordcard.state.TtsFrame
import java.lang.Math.abs


class WordBrowsingActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory((application as WordsApplication).repository)
    }

    private lateinit var tts: TextToSpeech

    private lateinit var ttsFrame: TtsFrame

    private var folderId: Int = 0

    private lateinit var wordListAdapter: WordFragmentStateAdapter

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
        wordListAdapter = WordFragmentStateAdapter(wordList, this)
        // セット
        viewpager.adapter = wordListAdapter
        // 100ページ生成
        viewpager.offscreenPageLimit = 100

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
            ttsFrame.getState().doStartOrStop(fragments, viewpager.currentItem, ttsFrame)
        }

        val tgAuto = findViewById<ToggleButton>(R.id.a05TbAuto)
        tgAuto.setOnCheckedChangeListener { _, isChecked ->
            val fragments = supportFragmentManager.fragments
            if (isChecked) {
                ttsFrame.getState().doAutoOn(fragments, viewpager.currentItem, ttsFrame)
            }
        }

        seekBar = findViewById(R.id.a05Sb)

        tts = TextToSpeech(this, this)

        ttsFrame = TtsFrame(this, tts, viewpager, supportFragmentManager)
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
                Log.d("TAG", "RUN_START_ACTIVITY")
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

    override fun onDestroy() {
        super.onDestroy()
        shutDown()
    }

    private fun shutDown() {
        tts.shutdown()
    }
}