package com.example.niwagner.postit

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.*
import android.widget.EditText
import android.support.v7.widget.Toolbar
import android.widget.Toast

class NoteFragment : Fragment() {

    public val TAG = NoteFragment::class.java.name
    private lateinit var mEditTextTitle: EditText
    private lateinit var mEditTextContent: EditText
    private lateinit var mFinishedButton: FloatingActionButton
    private lateinit var mCloseButton: FloatingActionButton
    private lateinit var mCallback: OnNewPostItListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_note, container, false)
        val toolbar = fragmentView.findViewById<Toolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.menu_note)
        toolbar.setNavigationIcon(R.drawable.ic_close)

        toolbar.setNavigationOnClickListener {
            mCallback.onClose()
        }

        toolbar.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }

        resolveUiElements(fragmentView)
        return fragmentView
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.finish -> {
                if (mEditTextTitle.text.toString().isNotEmpty() || mEditTextContent.text.toString().isNotEmpty()) {
                    mCallback.onPost(mEditTextTitle.text.toString(), mEditTextContent.text.toString())
                }

                else {
                    Toast.makeText(activity, "Please enter a valid note", Toast.LENGTH_SHORT).show()
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun resolveUiElements(fragmentView: View) {
        mEditTextTitle = fragmentView.findViewById(R.id.note_title)
        mEditTextContent = fragmentView.findViewById(R.id.note_content)
    }

    fun setCallback(callback: OnNewPostItListener) {
        mCallback = callback
    }

    interface OnNewPostItListener {
        fun onPost(title: String, content: String)

        fun onClose()
    }
}