package com.github.su_takizawa.wordcard

import android.app.Activity
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.view.View
import android.widget.*
import com.github.su_takizawa.wordcard.module.Util.Companion.getLangToSpinner
import com.github.su_takizawa.wordcard.module.Util.Companion.getSpinnerToLang
import com.github.su_takizawa.wordcard.module.Word
import com.google.gson.Gson


class WordEditActivity : EditBaseActivity() {

    private lateinit var editFrontWord: EditText
    private lateinit var editRearWord: EditText
    private lateinit var spFrontLang: Spinner
    private lateinit var spRearLang: Spinner

    private var frontLang: String = getSpinnerToLang(0)
    private var rearLang: String = getSpinnerToLang(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_edit)

        editFrontWord = findViewById(R.id.a04EtFrontWord)
        editRearWord = findViewById(R.id.a04EtRearWord)
        spFrontLang = findViewById(R.id.a04SpFrontLang)
        spRearLang = findViewById(R.id.a04SpRearLang)

        val mode = Mode.valueOf(intent.getStringExtra("MODE")!!)
        val word = Gson().fromJson(intent.getStringExtra("BODY"), Word::class.java) as Word
        if (mode == Mode.EDIT) {
            spFrontLang.setSelection(getLangToSpinner(word.frontLang))
            editFrontWord.setText(word.frontWord)
            spRearLang.setSelection(getLangToSpinner(word.rearLang))
            editRearWord.setText(word.rearWord)
        }

        spFrontLang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            /**
             * アイテムが選択された
             */
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val idx = spFrontLang.selectedItemPosition
                frontLang = getSpinnerToLang(idx)
            }

            /**
             * アイテムが選択されなかった
             */
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        spRearLang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            /**
             * アイテムが選択された
             */
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val idx = spRearLang.selectedItemPosition
                rearLang = getSpinnerToLang(idx)
            }

            /**
             * アイテムが選択されなかった
             */
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val btTranslate = findViewById<Button>(R.id.a04BtTranslate)
        btTranslate.setOnClickListener {
            if (getSupportedActivities().isNotEmpty()) {
                startActivity(createProcessTextIntent(editRearWord.text.toString()))
            } else {
                Toast.makeText(
                    applicationContext,
                    R.string.not_found_translate,
                    Toast.LENGTH_LONG
                ).show()
            }

        }

        val button = findViewById<Button>(R.id.a04BtSave)
        button.setOnClickListener {
            val replyIntent = Intent()
            if ((editFrontWord.text.toString() + editRearWord.text.toString()).isEmpty()) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val res = EditActivityRes(
                    mode,
                    Word(
                        word.id,
                        word.folderId,
                        frontLang,
                        editFrontWord.text.toString(),
                        rearLang,
                        editRearWord.text.toString()
                    )
                )
                replyIntent.putExtra(EXTRA_REPLY, Gson().toJson(res))
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    /**
     * 言語処理インテント設定
     */
    private fun createProcessTextIntent(text: String? = null): Intent {

        val intent = Intent()
            .setAction(Intent.ACTION_PROCESS_TEXT)
            .setType("text/plain")
        return text?.let {
            intent
                .putExtra(Intent.EXTRA_PROCESS_TEXT, it)
                .putExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
        } ?: intent
    }

    /**
     * インテントをサポートしているアクティビティ取得
     */
    private fun getSupportedActivities(): List<ResolveInfo> {
        val manager = applicationContext.packageManager
        return manager.queryIntentActivities(createProcessTextIntent(), 0)
    }

    companion object {
        const val EXTRA_REPLY = "com.github.su_takizawa.wordcard.REPLY"
    }
}