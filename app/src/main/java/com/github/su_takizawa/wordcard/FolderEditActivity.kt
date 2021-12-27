package com.github.su_takizawa.wordcard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import com.github.su_takizawa.wordcard.module.Folder
import com.google.gson.Gson

class FolderEditActivity : EditBaseActivity() {

    private lateinit var editWordView: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_edit)

        editWordView = findViewById(R.id.a02EtFolder)

        val mode = Mode.valueOf(intent.getStringExtra("MODE")!!)
        val folder = Gson().fromJson(intent.getStringExtra("BODY"), Folder::class.java) as Folder
        var folderId = 0
        if (mode == Mode.EDIT) {
            editWordView.setText(folder.folderName)
            folderId = folder.id
        }

        val button = findViewById<Button>(R.id.a02BtSave)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editWordView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val res = EditActivityRes(
                    mode,
                    Folder(folderId, editWordView.text.toString())
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