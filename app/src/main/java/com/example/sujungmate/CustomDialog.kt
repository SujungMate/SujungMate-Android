package com.dongmin.www.customdialog

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.sujungmate.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.setpassword_login_dialog.*

class CustomDialog(context : Context) {
    private val dialog = Dialog(context)

    fun myDialog(){
        dialog.setContentView(R.layout.setpassword_login_dialog)

        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        val setpassword = dialog.schoolemail_edittext_setpassword
        val btnDone = dialog.findViewById<Button>(R.id.setpassword_dialog_ok)
        val btnCancle = dialog.findViewById<Button>(R.id.setpassword_dialog_cancel)

        btnDone.setOnClickListener{
            onClickedListener.onOKClicked(setpassword.text.toString() + "@sungshin.ac.kr")
            dialog.dismiss()
        }
        btnCancle.setOnClickListener{
            dialog.dismiss()
        }
    }

    private lateinit var onClickedListener: MyDialogOKClickedListener

    fun setOnOKClickedListener(listener: MyDialogOKClickedListener) {
        onClickedListener = listener
    }


    interface MyDialogOKClickedListener {
        fun onOKClicked(studentNum : String)
    }
}
