package com.github.su_takizawa.wordcard.module

import android.util.Log
import com.github.su_takizawa.wordcard.EditActivityRes
import com.google.gson.Gson
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

class Util {
    companion object {
        /**
         * 総称型のクラスをデシリアライズする
         * @param json デシリアライズ対象文字列
         * @param tClass 総称型クラス
         * @return
         */
        fun <T> gsonResDecode(json: String, tClass: Class<T>): EditActivityRes<T> {
            val gson = Gson();
            val type: ParameterizedType = GenericOf(EditActivityRes::class.java, tClass)
            Log.v("TAG", "TYPE1:${type.rawType}");
            Log.v("TAG", "TYPE2:${type.actualTypeArguments[0]}");
            return gson.fromJson(json, type)
        }

        fun getSpinnerToLang(selectedItemNo: Int): String {
            return when (selectedItemNo) {
                0 -> Locale.JAPAN.language
                1 -> Locale.US.language
                2 -> Locale.FRANCE.language
                3 -> Locale.CHINESE.language
                // 韓国語
                4 -> "ko"
                // ベトナム語
                5 -> "vi"
                // タイ語
                6 -> "th"
                // ロシア語
                7 -> "ru"
                else -> throw RuntimeException("対象の選択肢がありません")
            }
        }

        fun getLangToSpinner(lang: String): Int {
            return when (lang) {
                Locale.JAPAN.language -> 0
                Locale.US.language -> 1
                Locale.FRANCE.language -> 2
                Locale.CHINESE.language -> 3
                // 韓国語
                "ko" -> 4
                // ベトナム語
                "vi" -> 5
                // タイ語
                "th" -> 6
                // ロシア語
                "ru" -> 7
                else -> 0//throw RuntimeException("対象の選択肢がありません")
            }
        }
    }
}

/**
 * ジェネリクス情報を保持するクラス
 */
class GenericOf<X, Y>(
    private val container: Class<X>,//
    private val wrapped: Class<Y>//
) : ParameterizedType {
    override fun getActualTypeArguments(): Array<Type> {
        return arrayOf(wrapped)
    }

    override fun getRawType(): Type {
        return container
    }

    override fun getOwnerType(): Type? {
        return null
    }
}
