package com.github.su_takizawa.wordcard

import androidx.appcompat.app.AppCompatActivity

abstract class EditBaseActivity : AppCompatActivity() {

    enum class Mode {
        ADD, EDIT
    }
}