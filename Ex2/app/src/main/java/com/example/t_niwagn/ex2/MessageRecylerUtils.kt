package com.example.t_niwagn.ex2

import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.recyclerview.extensions.ListAdapter
import android.widget.LinearLayout
import android.widget.TextView

class MessageRecylerUtils {

    class MessageHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

        private var mMessageText: TextView
        private var mLayout : LinearLayout

        init {
            v.setOnClickListener(this)
            this.mMessageText = v.findViewById(R.id.message_text2)
            this.mLayout = v.findViewById(R.id.message_id)
        }

        override fun onClick(v: View?) {
        }

        fun bind(message: String, position: Int) {
            mMessageText.text = message
            mMessageText.setTextColor(Color.parseColor("#4c4c4c"))
            if (position % 2 == 1) {
                mLayout.setBackgroundColor(Color.parseColor("#D3D3D3"))
            }
            else {
                mLayout.setBackgroundColor(Color.parseColor("#C0C0C0"))
            }
        }
    }

    class MessageCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(p0: String, p1: String): Boolean {
            return p0 == p1
        }

        override fun areContentsTheSame(p0: String, p1: String): Boolean {
            return p0 == p1
        }
    }

    class MessageAdapter : ListAdapter<String, MessageHolder>(MessageCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
            return MessageHolder(LayoutInflater.from(parent.context).
                inflate(R.layout.item_message, parent, false))
        }

        override fun onBindViewHolder(holder: MessageHolder, position: Int) {
            holder.bind(getItem(position), position)
        }
    }

}