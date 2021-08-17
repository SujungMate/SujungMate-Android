package com.example.sujungmate

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.sujungmate.R
import com.example.sujungmate.tables.SubDistinction

class SignUpActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up3)

        // EditText 입력 중 외부 터치 시 키보드 내리기 (닉네임)
        val outer_layout = findViewById<ConstraintLayout>(R.id.layout_signup3) // 레이아웃 가져오기
        outer_layout.setOnClickListener {
            hideKeyboard()
        }

        // 주전공에 대한 spinner 세팅
        SpinnerSettings(findViewById(R.id.major_spinner_signup3), R.array.interesting_major)

        val nextBtn = findViewById<Button>(R.id.next_button_signup3)
        nextBtn.setOnClickListener {

            // 1. 닉네임 (띄어쓰기 제거) ??? 중복 & 글자수 확인 ???
            var NICKNAME : String? = findViewById<EditText>(R.id.nickname_edittext_signup3).text.toString().trim()

            // 2. 주전공 ??? not null 확인 ???
            var MAJOR : String = findViewById<Spinner>(R.id.major_spinner_signup3).selectedItem.toString()

            // 이전 액티비티에서 학번 가져오기
            val subStudentID: Long = intent.getLongExtra("subStudentID", 0)
            Log.d("subStudentID : ", subStudentID.toString())

            // SignUpActivity4에게 특징 값 전달
            val intent = Intent(this, SignUpActivity4::class.java)
            intent.putExtra("sub_distinction", SubDistinction(NICKNAME.toString(), MAJOR, subStudentID))
            startActivity(intent)
        }
    }

    //키보드 숨기기
    fun hideKeyboard() {
        val editText1 = findViewById<EditText>(R.id.nickname_edittext_signup3)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText1.windowToken, 0)
    }

    //스피너
    private fun SpinnerSettings(spinner : Spinner, arrayId : Int) {
        ArrayAdapter.createFromResource(
            this,
            arrayId,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }
}