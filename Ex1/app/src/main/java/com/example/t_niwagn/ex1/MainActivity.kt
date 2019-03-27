package com.example.t_niwagn.ex1

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    private lateinit var userInput : EditText
    private lateinit var userText : TextView
    private lateinit var userButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.userInput = findViewById(R.id.user_input)
        this.userText = findViewById(R.id.user_text)
        this.userButton = findViewById(R.id.button)

        userButton.setOnClickListener {
            userText.text = userInput.text.toString()
            userInput.text.clear()
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(userInput.windowToken, 0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("USER_TEXT", userText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        userText.text = savedInstanceState?.getString("USER_TEXT")
    }
}
