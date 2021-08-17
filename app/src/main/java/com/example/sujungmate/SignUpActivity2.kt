package com.example.sujungmate

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.sujungmate.R

class SignUpActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up2)

        // EditText 입력 중 외부 터치 시 키보드 내리기
        val outer_layout = findViewById<ConstraintLayout>(R.id.layout_signup2) // 레이아웃 가져오기
        outer_layout.setOnClickListener {
            hideKeyboard()
        }

        val nextBtn = findViewById<Button>(R.id.next_button_signup2)
        nextBtn.setOnClickListener {

            // 학번 ??? not null 확인 & 인증번호 확인 ???
            var STUDENTID : String = findViewById<EditText>(R.id.schoolemail_edittext_signup2).text.toString().trim()

            val intent = Intent(this, SignUpActivity3::class.java)
            // SignUpActivity4로 학번 전달
            intent.putExtra("subStudentID", STUDENTID.toLong())
            startActivity(intent)
        }
    }

    //키보드 숨기기
    fun hideKeyboard() {
        val editText1 = findViewById<EditText>(R.id.schoolemail_edittext_signup2)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText1.windowToken, 0)
    }
}