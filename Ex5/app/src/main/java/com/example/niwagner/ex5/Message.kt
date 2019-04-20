package com.example.niwagner.ex5

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class Message(var mText: String = "", var mTimestamp: Date = Date(), var mId: String = "") : Comparable<Message> {

    override fun compareTo(other: Message): Int {
        return this.mTimestamp.compareTo(other.mTimestamp)
    }

    fun equals(p0: Message, p1: Message) : Boolean {
        return p0.mText.equals(p1.mText)
    }
}