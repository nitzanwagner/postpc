package com.example.niwagner.ex5

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater


class CustomProgressBar {

    var dialog: Dialog? = null
        private set

    fun show(context: Context) : Dialog {
        val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflator.inflate(R.layout.progress_bar, null)

        dialog = Dialog(context, R.style.NewDialog)
        dialog!!.setContentView(view)
        dialog!!.setCancelable(true)
        dialog!!.show()

        return dialog as Dialog
    }

}