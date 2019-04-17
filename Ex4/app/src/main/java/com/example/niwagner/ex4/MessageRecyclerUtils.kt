package com.example.niwagner.ex4

import android.graphics.Color
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.support.v7.app.AlertDialog
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore


class MessageRecyclerUtils {

    class MessageHolder(private val v: View) : RecyclerView.ViewHolder(v) {

        private var mMessage: TextView = v.findViewById(R.id.message_text)

        fun bind(message: Message, callback: MessageHolderCallback) {
            mMessage.text = message.mText
            mMessage.setTextColor(Color.parseColor("#4c4c4c"))
            createAlertDialog(v, message, callback)
        }

        private fun createAlertDialog(v: View, message: Message, callback: MessageHolderCallback) {
            v.setOnLongClickListener(View.OnLongClickListener {
                val adb = AlertDialog.Builder(v.context)
                adb.setTitle("Delete?")
                adb.setMessage("Are you sure you want to delete ${message.mText}?")
                adb.setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete") { _, _ -> callback.itemWasClickedToDelete(adapterPosition) }
                    .setCancelable(false)
                    .show()

                return@OnLongClickListener true
            })
        }
    }

    class MessageCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(p0: Message, p1: Message): Boolean {
            return p0 == p1
        }

        override fun areContentsTheSame(p0: Message, p1: Message): Boolean {
            return p0.mText == p1.mText
        }
    }

    class MessageAdapter : ListAdapter<Message, MessageHolder>(MessageCallback()) {

        private var mMessages: MutableList<Message>? = ArrayList<Message>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
            return MessageHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
            )
        }

        override fun onBindViewHolder(holder: MessageHolder, position: Int) {
            holder.bind(getItem(position), object : MessageHolderCallback {
                override fun itemWasClickedToDelete(position: Int) {
                    deleteMessageFromRemoteDB(getItem(position))
                    mMessages?.remove(getItem(position))
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, mMessages!!.size)
                }
            })
        }

        private fun deleteMessageFromRemoteDB(message: Message) {
            val db = FirebaseFirestore.getInstance()
            db.collection("messages").document(message.mId).delete()
                .addOnSuccessListener {
                    Log.d("TAG", "Message document with ID: ${message.mId} was deleted successfully")
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error deleting message document", e)
                }

        }

        override fun submitList(list: MutableList<Message>?) {
            super.submitList(list)
            mMessages = list
        }

        fun addToList(message: Message) {
            mMessages?.add(message)
            notifyDataSetChanged()
        }

        fun getList(): MutableList<Message>? {
            return mMessages
        }

    }

    interface MessageHolderCallback {
        fun itemWasClickedToDelete(position: Int)
    }
}