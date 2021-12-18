package com.github.su_takizawa.wordcard

/**
 * 編集アクティビティレスポンス
 * @param mode モード
 * @param item 要素
 */
data class EditActivityRes<T>(val mode: EditBaseActivity.Mode, val item:T){
}
