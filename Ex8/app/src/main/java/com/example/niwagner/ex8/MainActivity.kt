package com.example.niwagner.ex8

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.squareup.picasso.Picasso
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import okhttp3.RequestBody
import org.json.JSONException
import android.app.Activity
import android.view.inputmethod.InputMethodManager


class MainActivity : AppCompatActivity() {

    private val url = "http://hujipostpc2019.pythonanywhere.com"
    private val client = OkHttpClient()
    private lateinit var mUserToken : String
    private lateinit var mUsername : String
    private lateinit var mImageUrl : String
    private lateinit var mPrettyName : String
    private lateinit var mProgressBar : ProgressBar
    private lateinit var mWelcomeTextView : TextView
    private lateinit var mSubmitButton : Button
    private lateinit var mChangePrettyNameButton : Button
    private lateinit var mChangePrettyNameEditText : EditText
    private lateinit var mImageView : ImageView
    private lateinit var mSharedPreferences : SharedPreferences

    // Image buttons
    private lateinit var mUnicornButton : Button
    private lateinit var mAlienButton : Button
    private lateinit var mFrogButton : Button
    private lateinit var mRobotButton : Button
    private lateinit var mOctopusButton : Button
    private lateinit var mCrabButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        setInitialUIElements()

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        mUserToken = intent.getStringExtra("TOKEN")

        getUserInfo("$url/user")
    }

    private fun setInitialUIElements() {
        mProgressBar = findViewById(R.id.progress_bar)
        mWelcomeTextView = findViewById(R.id.welcome_textview)
        mChangePrettyNameButton = findViewById(R.id.button_change_pretty_name)
        mChangePrettyNameEditText = findViewById(R.id.change_pretty_name_edit_text)
        mImageView = findViewById(R.id.profilePic)
        mSubmitButton = findViewById(R.id.button_submit)
        mProgressBar.visibility = View.VISIBLE
        mWelcomeTextView.visibility = View.GONE
        mChangePrettyNameButton.visibility = View.GONE
        mChangePrettyNameEditText.visibility = View.GONE
        mSubmitButton.visibility = View.GONE
        mImageView.visibility = View.GONE
    }

    private fun getUserInfo(url: String) {
        val request = Request.Builder().url(url).header("Authorization", "token $mUserToken").build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonObject = JSONObject(response.body()?.string())
                mImageUrl = (jsonObject.get("data") as JSONObject).getString("image_url")
                mPrettyName = (jsonObject.get("data") as JSONObject).getString("pretty_name")
                mUsername = (jsonObject.get("data") as JSONObject).getString("username")
                setWelcomeUIElements()
            }
        })
    }

    private fun setWelcomeUIElements() {
        runOnUiThread {
            mProgressBar.visibility = View.GONE
            val textName = if (mPrettyName.isEmpty()) mUsername else mPrettyName
            mWelcomeTextView.text = getString(R.string.welcome_back, textName)
            mWelcomeTextView.visibility = View.VISIBLE
            mChangePrettyNameButton.visibility = View.VISIBLE
            Picasso.with(this).load("$url$mImageUrl").into(mImageView)
            mImageView.visibility = View.VISIBLE
        }

        mChangePrettyNameButton.setOnClickListener {
            mChangePrettyNameButton.visibility = View.GONE
            mChangePrettyNameEditText.visibility = View.VISIBLE
            mSubmitButton.visibility = View.VISIBLE
        }

        mSubmitButton.setOnClickListener {
            if (mChangePrettyNameEditText.text.toString().matches("[a-zA-Z0-9]+".toRegex())) {
                mPrettyName = mChangePrettyNameEditText.text.toString()
                runUpdateInfo()
                mChangePrettyNameButton.visibility = View.VISIBLE
                mChangePrettyNameEditText.visibility = View.GONE
                mSubmitButton.visibility = View.GONE
                mChangePrettyNameEditText.text.clear()
                hideKeyboard(mSubmitButton)

            } else {
                Toast.makeText(this, "Please enter a valid pretty name", Toast.LENGTH_SHORT).show()
            }
        }

        setImageButtonsListeners()
    }

    private fun setImageButtonsListeners() {
        mUnicornButton = findViewById(R.id.unicorn_button)
        mAlienButton = findViewById(R.id.alien_button)
        mFrogButton = findViewById(R.id.frog_button)
        mRobotButton = findViewById(R.id.robot_button)
        mOctopusButton = findViewById(R.id.octopus_button)
        mCrabButton = findViewById(R.id.crab_button)

        mUnicornButton.setOnClickListener {
            mImageUrl = "images/unicorn.png"
            runUpdateImage()
        }

        mAlienButton.setOnClickListener {
            mImageUrl = "images/alien.png"
            runUpdateImage()
        }

        mFrogButton.setOnClickListener {
            mImageUrl = "images/frog.png"
            runUpdateImage()
        }

        mRobotButton.setOnClickListener {
            mImageUrl = "images/robot.png"
            runUpdateImage()
        }

        mOctopusButton.setOnClickListener {
            mImageUrl = "images/octopus.png"
            runUpdateImage()
        }

        mCrabButton.setOnClickListener {
            mImageUrl = "images/crab.png"
            runUpdateImage()
        }
    }

    private fun update(itemName: String, itemValue: String, callback: Callback) {
        val JSON = MediaType.parse("application/json")
        val postdata = JSONObject()
        try {
            postdata.put(itemName, itemValue)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val body = RequestBody.create(JSON,  postdata.toString().toByteArray(Charsets.US_ASCII))
        val request = Request.Builder().url("$url/user/edit/").post(body)
            .header("Authorization", "token $mUserToken")
            .header("Content-Type", "application/json")
            .build()
        client.newCall(request).enqueue(callback)
    }

    private fun runUpdateInfo() {
        update("pretty_name", mPrettyName, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Something went wrong. Please try again later", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body()?.string()
                val jsonObject = JSONObject(res)
                mPrettyName = (jsonObject.get("data") as JSONObject).getString("pretty_name")
                mWelcomeTextView.text = getString(R.string.welcome_back, mPrettyName)
            }
        })
    }

    private fun runUpdateImage() {
        update("image_url", mImageUrl, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Something went wrong. Please try again later", Toast.LENGTH_LONG)
                        .show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    val res = response.body()?.string()
                    val jsonObject = JSONObject(res)
                    mImageUrl = (jsonObject.get("data") as JSONObject).getString("image_url")
                    Picasso.with(this@MainActivity).load("$url$mImageUrl").into(mImageView) }
            }
        })
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}