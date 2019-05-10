package com.example.niwagner.ex6

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager

class CallsBroadcastReceiver : BroadcastReceiver() {

    private lateinit var mNumberCall : String

    override fun onReceive(context: Context, intent: Intent?) {
        mNumberCall = intent?.getStringExtra(Intent.EXTRA_PHONE_NUMBER)!!
        val smsIntent = Intent(context, Settings::class.java).putExtra("EXTRA_PHONE_NUMBER", mNumberCall)
        context.startActivity(smsIntent)
    }

}