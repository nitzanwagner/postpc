package com.example.t_niwagn.ex3_chat

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private val SP_MESSAGES_LIST = "Messages"
    private val SP_LIST_LENGTH = "NumberOfMessages"
    private val EDIT_TEXT = "EditTextInput"


    private lateinit var mUserInput : EditText
    private lateinit var mUserButton : Button
    private var mAdapter = MessageRecylerUtils.MessageAdapter()
    private lateinit var mRecyclerView : RecyclerView
    private lateinit var mSharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prepareRecyclerViewChat()
        prepareEditTextUserInput()
        prepareButtonSend()

        setAdapterListFromSP()

        Log.d(SP_LIST_LENGTH, mAdapter.getList()?.size.toString())
    }

    private fun prepareRecyclerViewChat() {
        this.mRecyclerView = findViewById(R.id.messages_list_recycler_view)

        mRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mRecyclerView.addItemDecoration(DividerItemDecoration(mRecyclerView.context, DividerItemDecoration.VERTICAL))
        mRecyclerView.adapter = mAdapter

        mRecyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                hideKeyboard(mUserInput)
            }
        })
    }

    private fun prepareEditTextUserInput() {
        this.mUserInput = findViewById(R.id.user_input)
        mUserInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(mUserInput)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun prepareButtonSend() {
        this.mUserButton = findViewById(R.id.button)
        mUserButton.setOnClickListener {
            if (mUserInput.text.isBlank()) {
                Toast.makeText(this@MainActivity, "Please enter a valid message", Toast.LENGTH_SHORT).show()
                hideKeyboard(mUserInput)
            } else {
                mAdapter.addToList(Message(mUserInput.text.toString()))
                mUserInput.text.clear()
                hideKeyboard(mUserInput)
                saveListToSP(SP_MESSAGES_LIST)
            }
        }
    }

    private fun saveListToSP(key: String) {
        val json = Gson().toJson(mAdapter.getList())
        val editor = mSharedPreferences.edit()
        editor.putString(key, json).apply()
    }

    private fun setAdapterListFromSP() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val messagesList : MutableList<Message> = Gson().fromJson(mSharedPreferences.getString(SP_MESSAGES_LIST, null))
        mAdapter.submitList(messagesList)
    }

    private inline fun <reified T> Gson.fromJson(json: String?) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)!!

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(EDIT_TEXT, mUserInput.text.toString())
        saveListToSP(SP_MESSAGES_LIST)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            setAdapterListFromSP()
            mUserInput.setText(savedInstanceState.getString(EDIT_TEXT))
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
