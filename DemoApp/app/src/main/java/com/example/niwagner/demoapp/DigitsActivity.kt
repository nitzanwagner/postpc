package com.example.niwagner.demoapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText

class DigitsActivity : AppCompatActivity() {

    private lateinit var mDigits : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_text_digits)
        mDigits = findViewById(R.id.edit_text_digits)

        mDigits.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //hideKeyboard(mUserInput)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        mDigits.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val regex = """[0-9][0-9][0-9][0-9][0-9]""".toRegex()
                assert(regex.matches(s.toString()))
                if (regex.matches(s.toString())) {
                    val intentDigits = Intent().putExtra("digits", s.toString())
                    setResult(Activity.RESULT_OK, intentDigits)
                    finish()
                }
            }

        })
    }
}