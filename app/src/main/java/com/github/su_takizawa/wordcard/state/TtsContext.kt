package com.github.su_takizawa.wordcard.state

import android.speech.tts.TextToSpeech
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.github.su_takizawa.wordcard.WordBrowsingActivity
import com.github.su_takizawa.wordcard.WordFragment

interface TtsContext {
    val activity: WordBrowsingActivity
    val tts: TextToSpeech
    val viewPager: ViewPager2
    fun changeState(state: State)
    fun getState(): State
    fun speechText(fragments: List<Fragment>, currentItem: Int, lang: String, text: String)
    fun getMainLang(fragment: WordFragment): String

}

