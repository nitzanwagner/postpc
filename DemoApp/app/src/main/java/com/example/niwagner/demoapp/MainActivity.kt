package com.example.niwagner.demoapp

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var mFisherButton: Button
    private lateinit var mTapTextView: TextView
    private lateinit var mDigitsTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFisherButton = findViewById(R.id.go_fish_button)
        mTapTextView = findViewById(R.id.tap_on_me)
        mDigitsTextView = findViewById(R.id.digits_entered_text)
        mDigitsTextView.visibility = View.GONE

        mFisherButton.setOnClickListener(View.OnClickListener {
            val intentFisher = Intent(this, FisherActivity::class.java)
            startActivity(intentFisher)
        })

        mTapTextView.setOnClickListener {
            val intentDigits = Intent(this, DigitsActivity::class.java)
            startActivityForResult(intentDigits, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val numbersEntered = data!!.getStringExtra("digits")
                mDigitsTextView.text = getString(R.string.numbers_entered, numbersEntered)
                runOnUiThread{ mDigitsTextView.visibility = View.VISIBLE }
            }
        }
    }
}
