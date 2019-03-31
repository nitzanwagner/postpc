package com.example.t_niwagn.ex2

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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

        this.mUserInput = findViewById(R.id.user_input)
        this.mUserButton = findViewById(R.id.button)
        this.mRecyclerView = findViewById(R.id.messages_list_recycler_view)

        mRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mRecyclerView.addItemDecoration(DividerItemDecoration(mRecyclerView.context, DividerItemDecoration.VERTICAL))
        mRecyclerView.adapter = mAdapter

        mUserButton.setOnClickListener {
            if (mUserInput.text.isBlank()) {
                Toast.makeText(this@MainActivity, "Please enter a valid message", Toast.LENGTH_SHORT).show()
            } else {
                val cpyList = mMessagesList
                //cpyList?.add(mUserInput.text.toString())
                mMessagesList?.add(mUserInput.text.toString())
                //mMessagesList = cpyList
                mAdapter.submitList(mMessagesList)
                mAdapter.notifyDataSetChanged()
                val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                mUserInput.text.clear()
                inputMethodManager.hideSoftInputFromWindow(mUserInput.windowToken, 0)
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
            //val cpyList = mMessagesList
            //mMessagesList = cpyList
            mAdapter.submitList(mMessagesList)
            mAdapter.notifyDataSetChanged()
            mUserInput.setText(savedInstanceState.getString("savedStateKEY"))
        }
    }


}
