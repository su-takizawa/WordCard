package com.github.su_takizawa.wordcard.state

import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import com.github.su_takizawa.wordcard.R
import com.github.su_takizawa.wordcard.WordFragment


interface State {
    fun doStartOrStop(fragments: List<Fragment>, currentItem: Int, context: TtsContext)
    fun doAutoOn(fragments: List<Fragment>, currentItem: Int, context: TtsContext)
    fun doEndPlay(fragments: List<Fragment>, currentItem: Int, context: TtsContext)
}

class StopState private constructor() : State {
    companion object {
        private val singleton = StopState()
        fun getInstance() = singleton
    }

    override fun doStartOrStop(fragments: List<Fragment>, currentItem: Int, context: TtsContext) {
        playProcess(fragments, currentItem, context)
    }

    override fun doAutoOn(fragments: List<Fragment>, currentItem: Int, context: TtsContext) {
        playProcess(fragments, currentItem, context)
    }

    override fun doEndPlay(fragments: List<Fragment>, currentItem: Int, context: TtsContext) {
        //ๅฆ็็กใ
    }

    private fun playProcess(fragments: List<Fragment>, currentItem: Int, context: TtsContext) {
        context.activity.findViewById<Button>(R.id.a05BtPlay).background =
            context.activity.getDrawable(R.drawable.ic_baseline_pause_circle_filled_24)
        val fragment =
            context.supportFragmentManager.findFragmentByTag("f$currentItem") as WordFragment
        fragment.view?.findViewById<TextView>(R.id.a05TvItem)?.let { tvItem ->
            val lang = context.getMainLang(fragment)
            context.speechText(fragments, currentItem, lang, tvItem.text.toString())
            context.changeState(PlayState.getInstance())
        }

    }
}

class PlayState private constructor() : State {
    companion object {
        private val singleton = PlayState()
        fun getInstance() = singleton
    }

    override fun doStartOrStop(fragments: List<Fragment>, currentItem: Int, context: TtsContext) {
        Log.v("TAG", "PlayState_DoStartOrStop")
        context.tts.stop()
    }

    override fun doAutoOn(fragments: List<Fragment>, currentItem: Int, context: TtsContext) {
        //ๅฆ็็กใ
    }

    override fun doEndPlay(fragments: List<Fragment>, currentItem: Int, context: TtsContext) {
        val fragment =
            context.supportFragmentManager.findFragmentByTag("f$currentItem") as WordFragment
        val tbAuto = context.activity.findViewById<ToggleButton>(R.id.a05TbAuto)
        val btPlay = context.activity.findViewById<Button>(R.id.a05BtPlay)
        Log.v("TAG", "PlayState_doEndPlay:${tbAuto.isChecked},${fragment.isRear}")
        Log.v(
            "TAG",
            "PlayState_doEndPlay:currentItem:${currentItem},viewPager.adapter.itemCount:${context.viewPager.adapter?.itemCount?.let { it - 1 } ?: 0}"
        )
        Log.v(
            "TAG",
            "PlayState_doEndPlay_detail:${tbAuto.isChecked},${fragment.isRear && currentItem == context.viewPager.adapter?.itemCount?.let { it - 1 } ?: 0 - 1}"
        )
        when (tbAuto.isChecked) {
            true -> {
                if (fragment.isRear && currentItem == context.viewPager.adapter?.itemCount?.let { it - 1 } ?: 0) {
                    Log.v("TAG", "PlayState_doEndPlayCase:1-1")
                    btPlay.background =
                        context.activity.getDrawable(R.drawable.ic_baseline_play_circle_filled_24)
                    val mainHandler = android.os.Handler(Looper.getMainLooper())
                    mainHandler.post {
                        tbAuto.toggle()
                    }
                    context.changeState(StopState.getInstance())
                } else if (!fragment.isRear) {
                    Log.v("TAG", "PlayState_doEndPlayCase:1-2")
                    fragment.view?.findViewById<TextView>(R.id.a05TvItem)?.let { tvItem ->
                        val mainHandler = android.os.Handler(Looper.getMainLooper())
                        mainHandler.post {
                            tvItem.performClick()
                            val lang = context.getMainLang(fragment)
                            context.speechText(fragments, currentItem, lang, tvItem.text.toString())
                        }
                    }
                } else if (fragment.isRear) {
                    Log.v("TAG", "PlayState_doEndPlayCase:1-3")
                    val mainHandler = android.os.Handler(Looper.getMainLooper())
                    mainHandler.post {
                        Log.v(
                            "TAG",
                            "PlayState_doEndPlayCase:1-3:currentItem_Before:${context.viewPager.currentItem}"
                        )
                        context.viewPager.currentItem++
                        Log.v(
                            "TAG",
                            "PlayState_doEndPlayCase:1-3:currentItem_after:${context.viewPager.currentItem}"
                        )
                        val currentItem = context.viewPager.currentItem
                        val fragments = context.supportFragmentManager.fragments
                        val fragment =
                            context.supportFragmentManager.findFragmentByTag("f$currentItem") as WordFragment
                        fragment.view?.findViewById<TextView>(R.id.a05TvItem)
                            ?.let { tvItem ->
                                val lang = context.getMainLang(fragment)
                                context.speechText(
                                    fragments,
                                    currentItem,
                                    lang,
                                    tvItem.text.toString()
                                )
                            }
                    }
                }
            }
            false -> {
                Log.v("TAG", "PlayState_doEndPlayCase:2")
                btPlay.background =
                    context.activity.getDrawable(R.drawable.ic_baseline_play_circle_filled_24)
                context.changeState(StopState.getInstance())
            }
        }
    }
}
