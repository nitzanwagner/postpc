package com.example.t_niwagn.ex2

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*

class MainActivity : AppCompatActivity() {

    private lateinit var mUserInput : EditText
    private lateinit var mUserButton : Button
    private var mMessagesList : ArrayList<String>? =  ArrayList()
    private var mAdapter = MessageRecylerUtils.MessageAdapter()
    private lateinit var mRecyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prepareRecyclerViewChat()
        prepareEditTextUserInput()
        prepareButtonSend()
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
        mUserInput.setOnEditorActionListener {v, actionId, event ->
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
                val cpyList = mMessagesList
                cpyList?.add(mUserInput.text.toString())
                mMessagesList = cpyList
                mAdapter.submitList(cpyList)
                mUserInput.text.clear()
                hideKeyboard(mUserInput)
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putStringArrayList("RecyclerViewItems", mMessagesList)
        outState?.putString("savedStateKEY", mUserInput.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            mMessagesList = savedInstanceState.getStringArrayList("RecyclerViewItems")
            val cpyList = mMessagesList
            mMessagesList = cpyList
            mAdapter.submitList(cpyList)
            mUserInput.setText(savedInstanceState.getString("savedStateKEY"))
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


}
