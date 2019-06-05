package com.example.niwagner.ex8

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class LoginActivity : AppCompatActivity() {

    //private val url = "http://hujipostpc2019.pythonanywhere.com"
    private val url = "http://172.29.97.84:5678"
    private val client = OkHttpClient()
    private lateinit var mUserToken : String
    private lateinit var mAnimationView : LottieAnimationView
    private lateinit var mSharedPreferences : SharedPreferences
    private lateinit var mUsernameEditText : EditText
    private lateinit var mButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        mAnimationView = findViewById(R.id.animation)
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        runCheckConnection(url)
        if (mSharedPreferences.getString("USERNAME", "")!!.isEmpty()) {
            askForUsername()
        } else {
            val username = mSharedPreferences.getString("USERNAME", "")
            runGetToken("$url/users/$username/token/")
        }
    }

    private fun askForUsername() {
        mUsernameEditText = findViewById(R.id.username)
        mButton = findViewById(R.id.button)
        mButton.setOnClickListener{
            val username = mUsernameEditText.text.toString()
            if (username.matches("[a-zA-Z0-9]+".toRegex())) {
                mSharedPreferences.edit().putString("USERNAME", username).apply()
                runGetToken("$url/users/$username/token/")
            }
        }
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun runCheckConnection(url: String) {
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    mAnimationView.setAnimation("success-animation.json")
                    mAnimationView.playAnimation()
                    mAnimationView.clearAnimation()
                    //mAnimationView.cancelAnimation()
                }
            }
        })
    }

    private fun runGetToken(url: String) {
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body()?.string()
                val jsonObject = JSONObject(res)
                mUserToken = jsonObject.getString("data")
                val activityIntent = Intent(this@LoginActivity, MainActivity::class.java)
                    .putExtra("TOKEN", mUserToken)
                startActivity(activityIntent)
            }
        })
    }
}
