package com.example.niwagner.ex7

import android.Manifest.permission.*
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.content.DialogInterface
import android.widget.Toast
import android.Manifest.permission.SEND_SMS
import android.app.Activity
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.telephony.SmsManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.app.PendingIntent
import android.content.Intent

class Settings : AppCompatActivity() {

    private val PERMISSION_ALL = 1
    private val permissions = arrayOf(READ_PHONE_STATE, PROCESS_OUTGOING_CALLS, SEND_SMS)
    private lateinit var mSharedPreferences : SharedPreferences
    private lateinit var mNumber: EditText
    private lateinit var mMessage: EditText
    private lateinit var mTextView : TextView
    private var mEditedNumber = false
    private var mEditedMessage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        mNumber = findViewById(R.id.editNumber)
        mMessage = findViewById(R.id.editMessage)
        mTextView = findViewById(R.id.textView)
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        if (!hasPermissions(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL)
        }

        setTextView()
        prepareFields()

        if (intent != null && intent.getStringExtra("EXTRA_PHONE_NUMBER") != null) {
            sendSms(intent.getStringExtra("EXTRA_PHONE_NUMBER"))
        }
    }

    private fun prepareFields() {
        mNumber.setText(mSharedPreferences.getString("NUMBER", ""))
        mMessage.setText(mSharedPreferences.getString("MESSAGE", resources.getString(R.string.enter_message)))

        mNumber.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(mNumber)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        mMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(mMessage)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        mNumber.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable) {
                mEditedMessage = !s.isBlank()
                mSharedPreferences.edit().putString("NUMBER", s.toString()).apply()
                setTextView()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        mMessage.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable) {
                mEditedMessage = !s.isBlank()
                mSharedPreferences.edit().putString("MESSAGE", s.toString()).apply()
                setTextView()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setTextView() {
        if (mEditedNumber && mEditedMessage) {
            runOnUiThread { mTextView.text = getString(R.string.ready_sms) }
        }

        else if (mSharedPreferences.getString("NUMBER", "") != "" &&
            mSharedPreferences.getString("MESSAGE", resources.getString(R.string.enter_message)) != "") {
            runOnUiThread { mTextView.text = getString(R.string.ready_sms) }
        }

        else {
            runOnUiThread { mTextView.text = getString(R.string.missing_info)}
        }
    }

    private fun checkAndRequestPermissions() {
        if (!hasPermissions(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL)
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_ALL -> {

                val perms = HashMap<String, Int>()
                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    if (shouldStartApp(perms)) {
                        initializeFields(true)
                    }
                    else {
                        initializeFields(false)
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_PHONE_STATE)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, PROCESS_OUTGOING_CALLS)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, SEND_SMS)) {
                            showDialogOK("READ PHONE STATE, PROCESS OUTGOING CALLS and SEND SMS Permission are required for this app")
                        } else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }

            }
        }

    }


    private fun showDialogOK(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("Try again") { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                    DialogInterface.BUTTON_NEGATIVE -> {
                    }
                }
            }.create()
            .show()
    }

    private fun shouldStartApp(perms : HashMap<String, Int>) : Boolean {
        if (perms[READ_PHONE_STATE] == PackageManager.PERMISSION_GRANTED
            && perms[PROCESS_OUTGOING_CALLS] == PackageManager.PERMISSION_GRANTED
            && perms[SEND_SMS] == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    private fun initializeFields(visibility : Boolean) {
        if (visibility) {
            mNumber.visibility = View.VISIBLE
            mMessage.visibility = View.VISIBLE
            mTextView.visibility = View.VISIBLE
        } else {
            mNumber.visibility = View.GONE
            mMessage.visibility = View.GONE
            mTextView.visibility = View.GONE
        }
    }

    private fun sendSms(callNumber : String) {
        val smsManager = SmsManager.getDefault()
        val sentIntent = Intent(this, SentSMSIntent::class.java).putExtra("NOTIFICATION_ID", 1)
        val sentPendingIntent = PendingIntent.getService(this, 0, sentIntent, 0)
        val deliverIntent = Intent(this, DeliverSMSIntent::class.java).putExtra("NOTIFICATION_ID", 1)
        val deliverPendingIntent = PendingIntent.getService(this, 0, deliverIntent, 0)
        smsManager.sendTextMessage(mNumber.text.toString(), null, mMessage.text.toString() + callNumber,
            sentPendingIntent, deliverPendingIntent)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("NUMBER", mNumber.text.toString())
        outState?.putString("MESSAGE", mMessage.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            mNumber.setText(savedInstanceState.getString("NUMBER"))
            mMessage.setText(savedInstanceState.getString("MESSAGE"))
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}