package com.example.niwagner.ex7

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat

class CallsBroadcastReceiver : BroadcastReceiver() {

    private val CHANNEL_ID = "stalkerChannel"
    private lateinit var mNumberCall : String
    private lateinit var mContext : Context

    override fun onReceive(context: Context, intent: Intent?) {
        mContext = context
        mNumberCall = intent?.getStringExtra(Intent.EXTRA_PHONE_NUMBER)!!

        createNotificationChannel()
        sendNotification()

        val smsIntent = Intent(context, Settings::class.java).putExtra("EXTRA_PHONE_NUMBER", mNumberCall)
        context.startActivity(smsIntent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "sms"
            val descriptionText = "send sms"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification() {
        val builder = NotificationCompat.Builder(mContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Stalker App Notification")
            .setContentText("Sending message...")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(mContext)) {
            notify(1, builder.build()) }
    }
}

class SentSMSIntent : IntentService("SendSMSIntent") {

    private val CHANNEL_ID = "stalkerChannel"
    override fun onHandleIntent(intent: Intent?) {
        val notificationId = intent?.getIntExtra("NOTIFICATION_ID", 0)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentText("Message sent successfully!")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Stalker App Notification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId!!, builder.build()) }
    }
}

class DeliverSMSIntent : IntentService("DeliverSMSIntent") {

    private val CHANNEL_ID = "stalkerChannel"
    override fun onHandleIntent(intent: Intent?) {
        val notificationId = intent?.getIntExtra("NOTIFICATION_ID", 0)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentText("Message received successfully!")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Stalker App Notification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            notify(notificationId!!, builder.build()) }    }
}
