package com.example.niwagner.ex5

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class MessageRecyclerUtils {

    class MessageHolder(private val v: View) : RecyclerView.ViewHolder(v) {

        private var mMessage: TextView = v.findViewById(R.id.message_text)

        fun bind(message: Message) {
            mMessage.text = message.mText
            mMessage.setTextColor(Color.parseColor("#4c4c4c"))
        }

        class MessageCallback : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(p0: Message, p1: Message): Boolean {
                return p0 == p1
            }

            override fun areContentsTheSame(p0: Message, p1: Message): Boolean {
                return p0.mText == p1.mText
            }
        }
    }

    class MessageAdapter(context: Activity) : ListAdapter<Message, MessageHolder>(MessageHolder.MessageCallback()) {

        private var mMessages: MutableList<Message>? = ArrayList<Message>()
        private val mContext = context

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
            return MessageHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
            )
        }

        override fun onBindViewHolder(holder: MessageHolder, position: Int) {
            holder.bind(getItem(position))
            holder.itemView.setOnLongClickListener(View.OnLongClickListener {
                val curMessage = getItem(position)
                val intentMessageDetails = Intent(mContext, MessageDetailsActivity::class.java)
                    .putExtra("position", position)
                    .putExtra("text", curMessage.mText)
                    .putExtra("id", curMessage.mId)
                    .putExtra("phone", curMessage.mPhoneId)
                    .putExtra("timestamp", curMessage.mTimestamp.toString())
                mContext.startActivityForResult(intentMessageDetails, 1)

                return@OnLongClickListener true
            })
        }

        fun deleteMessage(position: Int) {
            mMessages?.remove(getItem(position))
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, mMessages!!.size)
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
}