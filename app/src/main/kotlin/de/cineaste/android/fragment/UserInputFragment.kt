package de.cineaste.android.fragment

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

import de.cineaste.android.R

class UserInputFragment : DialogFragment(), TextView.OnEditorActionListener, View.OnClickListener {

    private lateinit var editText: EditText

    interface UserNameListener {
        fun onFinishUserDialog(userName: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_user_input, container)
        val okBtn = view.findViewById<Button>(R.id.ok_tv)
        editText = view.findViewById(R.id.username_et)

        okBtn.setOnClickListener(this)
        editText.setOnEditorActionListener(this)
        editText.requestFocus()

        dialog.window?.let {
            it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            dialog.setTitle(R.string.enter_username)
            dialog.setCancelable(false)
        }

        return view
    }

   override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        getNameAndDismiss()

        return true
    }

    override fun onClick(v: View) {
        getNameAndDismiss()
    }

    private fun getNameAndDismiss() {
        val input = editText.text.toString().trim { it <= ' ' }

        if (input.isNotEmpty()) {
            val activity = activity as UserNameListener? ?: return
            activity.onFinishUserDialog(input)
            this.dismiss()
        }
    }
}
