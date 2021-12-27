package com.github.su_takizawa.wordcard.state

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.github.su_takizawa.wordcard.R
import com.github.su_takizawa.wordcard.WordFragment
import java.util.*

class TtsFrame(
    override val activity: com.github.su_takizawa.wordcard.WordBrowsingActivity,//
    override val tts: android.speech.tts.TextToSpeech,//
    override val viewPager: androidx.viewpager2.widget.ViewPager2,//
    override val supportFragmentManager: androidx.fragment.app.FragmentManager,//
) :
    TtsContext {
    private var state: State = StopState.getInstance()

    override fun changeState(state: State) {
        this.state = state
    }

    override fun getState(): State {
        return state
    }

    /**
     * テキスト発声
     */
    override fun speechText(
        fragments: List<Fragment>,
        currentItem: Int,
        lang: String,
        text: String
    ) {
        Log.v("Text2Speech", "isSpeaking:${tts.isSpeaking}")
        if (text.isNotEmpty()) {
            val result = tts.setLanguage(Locale(lang))
            Log.e("Text2Speech", "$lang,$result debug")
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                //言語データがダウンロードされていません、Wifi環境へ繋ぐかもしくは設定画面よりダウンロードしてください。
                //お使いの端末ではこの言語はサポートされていません。
                Log.e("Text2Speech", "$result is not supported")
            }
            setSpeechRate()
            setSpeechPitch()
            setTtsListener(fragments, currentItem)
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "messageID")
        }
    }

    /**
     * 主言語を取得
     */
    override fun getMainLang(fragment: WordFragment): String {
        return when (fragment.isRear) {
            false -> fragment.word.frontLang
            true -> fragment.word.rearLang
        }
    }

    /**
     * 読み上げのスピード
     */
    private fun setSpeechRate() {
        val progress = activity.findViewById<SeekBar>(R.id.a05Sb).progress
        tts.setSpeechRate(progress / 10.toFloat())
    }

    /**
     * 読み上げのピッチ
     */
    private fun setSpeechPitch() {
        tts.setPitch(1.0.toFloat())
    }

    /**
     * 読み上げの終わり,エラー,始まり,停止を取得
     */
    private fun setTtsListener(fragments: List<Fragment>, currentItem: Int) {
        val listenerResult =
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onDone(utteranceId: String) {
                    Log.d("TAG", "progress on Done $utteranceId")
                    getState().doEndPlay(fragments, currentItem, this@TtsFrame)
                }

                override fun onError(utteranceId: String) {
                    Log.d("TAG", "progress on Error $utteranceId")
                }

                override fun onStart(utteranceId: String) {
                    Log.d("TAG", "progress on Start $utteranceId")
                }

                override fun onStop(utteranceId: String?, interrupted: Boolean) {
                    Log.d("TAG", "progress on Stop $utteranceId")
                    getState().doEndPlay(fragments, currentItem, this@TtsFrame)

                }
            })
        if (listenerResult != TextToSpeech.SUCCESS) {
            Log.e("TAG", "failed to add utterance progress listener")
        }
    }
}