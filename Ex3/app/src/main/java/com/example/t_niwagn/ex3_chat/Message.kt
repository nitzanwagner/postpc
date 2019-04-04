package com.example.t_niwagn.ex3_chat

class Message(var mMessageText: String) {

    fun equals(p0: Message, p1: Message) : Boolean {
        return p0.mMessageText.equals(p1.mMessageText)
    }
}