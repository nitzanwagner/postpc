package com.example.niwagner.ex5

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import android.app.Activity
import android.content.Intent
import org.w3c.dom.Text


class MessageDetailsActivity : AppCompatActivity() {

    private lateinit var mMessageText : TextView
    private lateinit var mTimestamp : TextView
    private lateinit var mPhoneNum : TextView
    private lateinit var mButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.message_details)

        mMessageText = findViewById(R.id.textmessage)
        mTimestamp = findViewById(R.id.timestamp)
        mPhoneNum = findViewById(R.id.phone)

        mMessageText.text = getString(R.string.message, intent.getStringExtra("text"))
        mTimestamp.text = getString(R.string.timestamp, intent.getStringExtra("timestamp"))
        mPhoneNum.text = getString(R.string.phone_num, intent.getStringExtra("phone"))

        prepareDeleteButton()
    }

    private fun prepareDeleteButton() {
        this.mButton = findViewById(R.id.delete_button)
        mButton.setOnClickListener {
            val position = intent.getIntExtra("position", 0)
            val id = intent.getStringExtra("id")
            val intent = Intent()
            intent.putExtra("position", position).putExtra("id", id).putExtra("delete", true)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}