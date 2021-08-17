package com.example.sujungmate

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.sujungmate.LoginActivity
import com.example.sujungmate.R
import com.example.sujungmate.tables.Distinction
import com.example.sujungmate.tables.SubDistinction
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity4 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up4)

        // 수강 과목 editText 입력 중 외부 터치 시 키보드 내리기
        val outer_layout = findViewById<ConstraintLayout>(R.id.layout_signUp4)
        outer_layout.setOnClickListener {
            hideKeyboard()
        }

        // MBTI, 관심사에 대한 spinner 세팅
        SpinnerSettings(findViewById(R.id.mbti_spinner_signup4), R.array.MBTI_type)
        SpinnerSettings(findViewById(R.id.topinterest_spinner_signup4), R.array.large_category)

        // 회원가입 버튼 클릭 시 특징 데이터 추가
        findViewById<View>(R.id.signup_button_signup4).setOnClickListener {

            // 1. 수강과목 (띄어쓰기 제거)
            var LECTURE : String? = findViewById<EditText>(R.id.lecture1_spinner_signup4).text.toString().trim()

            // 2.MBTI
            var MBTI : String = findViewById<Spinner>(R.id.mbti_spinner_signup4).selectedItem.toString()

            // 3. 관심사
            var INTEREST : String? = findViewById<Spinner>(R.id.topinterest_spinner_signup4).selectedItem.toString()

            // 이전 액티비티에서 학번, 닉네임, 주전공 가져오기
            var subdistinction : SubDistinction? = intent.getParcelableExtra("sub_distinction")
            var STUDENTID : Long? = subdistinction?.subStudentID
            var NICKNAME : String? = subdistinction?.subNickname
            var MAJOR : String? = subdistinction?.subMajor

            Log.d("subdistinction : ", subdistinction.toString())

            // firebase 데이터 저장
            upload(LECTURE, MBTI, INTEREST.toString(), STUDENTID, NICKNAME.toString(), MAJOR.toString())

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    fun upload(lecture : String?, mbti : String, interest : String, studentID : Long?, nickname : String, major : String) {
        // Distinction(특징) 데이터 클래스 생성
        var distinction: Distinction = Distinction()

        distinction.studentID = studentID
        distinction.nickname = nickname
        distinction.major = major
        distinction.lecture = lecture
        distinction.interest = interest
        distinction.mbti = mbti

        // Firebase DB에 특징 저장 - !!! 추후 회원 내부로 경로 변경 필요 !!!
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val myRef : DatabaseReference = database.getReference("distinctions")
        myRef.setValue(distinction)
    }

    //키보드 숨기기
    fun hideKeyboard() {
        val editText1 = findViewById<EditText>(R.id.lecture1_spinner_signup4)
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