package com.github.su_takizawa.wordcard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.github.su_takizawa.wordcard.module.Word
import com.google.gson.Gson

class WordEditActivity : EditBaseActivity() {

    private lateinit var editFrontWord: EditText
    private lateinit var editRearWord: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_edit)

        editFrontWord = findViewById(R.id.s04EtFrontWord)
        editRearWord = findViewById(R.id.s04EtRearWord)

        val mode = Mode.valueOf(intent.getStringExtra("MODE")!!)
        val word = Gson().fromJson(intent.getStringExtra("BODY"), Word::class.java) as Word
        if (mode == Mode.EDIT) {
            editFrontWord.setText(word.frontWord)
            editRearWord.setText(word.rearWord)
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
                        "",
                        editFrontWord.text.toString(),
                        "",
                        editRearWord.text.toString()
                    )
                )
                replyIntent.putExtra(EXTRA_REPLY, Gson().toJson(res))
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    companion object {
        const val EXTRA_REPLY = "com.github.su_takizawa.wordcard.REPLY"
    }
}