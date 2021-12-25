package com.github.su_takizawa.wordcard

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.ViewPager2
import java.lang.Math.abs


class WordBrowsingActivity : AppCompatActivity() {

    private var folderId: Int = 0

    private lateinit var viewpager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_browsing)

        folderId = intent.getStringExtra("FOLDER_ID")!!.toInt();

        // ViewPager2のインスタンス化
        viewpager = findViewById(R.id.viewpager)
        // ページインスタンスを用意
        val pagerAdapter = PagerAdapter(this)
        // セット
        viewpager.adapter = pagerAdapter

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

    private inner class PagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        // ページ数を取得
        override fun getItemCount(): Int = 3

        // スワイプ位置によって表示するFragmentを変更
        override fun createFragment(position: Int): Fragment =
            when (position) {
                0 -> {
                    WordFragment.newInstance("", "")
                }
                1 -> {
                    WordFragment.newInstance("", "")
                }
                2 -> {
                    WordFragment.newInstance("", "")
                }
                else -> {
                    WordFragment.newInstance("", "")
                }
            }
    }
}