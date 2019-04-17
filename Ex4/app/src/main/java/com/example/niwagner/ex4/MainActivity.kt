package com.example.niwagner.ex4

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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val SP_MESSAGES_LIST = "Messages"
    private val SP_LIST_LENGTH = "NumberOfMessages"
    private val EDIT_TEXT = "EditTextInput"
    private val NOT_FIRST_LAUNCH = "FirstLaunch"

    private lateinit var mUserInput: EditText
    private lateinit var mUserButton: Button
    private var mAdapter = MessageRecyclerUtils.MessageAdapter()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSharedPreferences: SharedPreferences
    private var mExecutor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        prepareRecyclerViewChat()
        prepareEditTextUserInput()
        prepareButtonSend()

        if (!mSharedPreferences.getBoolean(NOT_FIRST_LAUNCH, false)) {
            setAdapterListFromRemoteDB()
            mSharedPreferences.edit().putBoolean(NOT_FIRST_LAUNCH, true).apply()
        } else {
            setAdapterListFromSP()
        }

        Log.d(SP_LIST_LENGTH, mAdapter.getList()?.size.toString())
    }

    private fun prepareRecyclerViewChat() {
        this.mRecyclerView = findViewById(R.id.messages_list_recycler_view)

        mRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mRecyclerView.addItemDecoration(DividerItemDecoration(mRecyclerView.context, DividerItemDecoration.VERTICAL))
        mRecyclerView.adapter = mAdapter

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                val message = Message(mUserInput.text.toString(), Date(), UUID.randomUUID().toString())
                mAdapter.addToList(message)
                mUserInput.text.clear()
                hideKeyboard(mUserInput)
                saveListToSP(SP_MESSAGES_LIST)
                addMessageToRemoteDB(message)
            }
        }
    }

    private fun addMessageToRemoteDB(message: Message) {
        val db = FirebaseFirestore.getInstance()
        db.collection("messages").document(message.mId).set(message)
            .addOnSuccessListener {
                Log.d("TAG", "Message document with ID: ${message.mId} was added successfully")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding message document", e)
            }
    }

    private fun saveListToSP(key: String) {
        val editor = mSharedPreferences.edit()
        editor.putString(key, Gson().toJson(mAdapter.getList()) ?: "").apply()
    }

    private fun setAdapterListFromSP() {
        val mType = object : TypeToken<MutableList<Message>>() {}.type
        mExecutor.execute {
            val messagesList = Gson().fromJson<MutableList<Message>>(mSharedPreferences.getString(SP_MESSAGES_LIST, ""), mType)
            mAdapter.submitList(messagesList ?: ArrayList())
        }
    }

    private fun setAdapterListFromRemoteDB() {
        val messagesList = ArrayList<Message>()
        val db = FirebaseFirestore.getInstance()
        db.collection("messages").get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    val message = doc.toObject(Message::class.java)
                    messagesList.add(message)
                }
                messagesList.sort()
                mAdapter.submitList(messagesList)
            }.addOnFailureListener { exception ->
                Log.d("TAG", "Error getting messages: ", exception)
            }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(EDIT_TEXT, mUserInput.text.toString())
        saveListToSP(SP_MESSAGES_LIST)
        mExecutor.shutdown()
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

}