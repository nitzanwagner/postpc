package com.example.niwagner.ex5

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {

    private val EDIT_TEXT = "EditTextInputSignIn"

    private lateinit var mUserInput: EditText
    private lateinit var mButtonName : Button
    private lateinit var mSkipButton : Button
    private lateinit var mSharedPreferences: SharedPreferences

    private lateinit var mProgressBar: CustomProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in)

        mProgressBar = CustomProgressBar()
        mProgressBar.show(this)

        FirebaseFirestore.getInstance().collection("defaults").document("user").get()
            .addOnSuccessListener { result ->
                val user = result?.get("userName")
                if (user != null) {
                    goToMainActivity()
                }

                mProgressBar.dialog?.dismiss()
            }.addOnFailureListener { exception ->
                Log.d("TAG", "Error getting messages: ", exception) }

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        prepareNameButton()
        prepareSkipButton()
        prepareEditTextUserInput()
    }

    private fun prepareNameButton() {
        this.mButtonName = findViewById(R.id.button_name)
        mButtonName.visibility = View.GONE
        mButtonName.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            db.collection("defaults").document("user").set(User(mUserInput.text.toString()))
                .addOnCompleteListener {
                }

            goToMainActivity()
        }
    }

    private fun prepareSkipButton() {
        this.mSkipButton = findViewById(R.id.button_skip)
        mSkipButton.setOnClickListener {
            goToMainActivity()
        }
    }

    private fun prepareEditTextUserInput() {
        this.mUserInput = findViewById(R.id.sign_in_edit_text)
        mUserInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(mUserInput)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        mUserInput.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable) {
                setTextView(s)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setTextView(s)
            }

            fun setTextView(s: CharSequence) {
                if (s.isBlank()) {
                    runOnUiThread{ mButtonName.visibility = View.GONE }
                } else {
                    runOnUiThread{ mButtonName.visibility = View.VISIBLE }
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(EDIT_TEXT, mUserInput.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            mUserInput.setText(savedInstanceState.getString(EDIT_TEXT))
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        mProgressBar.dialog?.dismiss()
        startActivity(intent)
        finish()
    }
}

class User(var userName: String)