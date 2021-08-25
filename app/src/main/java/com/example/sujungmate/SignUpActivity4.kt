package com.example.sujungmate

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.sujungmate.messages.ChatManageActivity
import com.example.sujungmate.tables.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up4.*

class SignUpActivity4 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up4)

        // 뒤로가기 버튼
        val toolbar = findViewById<Toolbar>(R.id.toolbar_signup4)
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)

        // 채연이 코드
        // 수강 과목 editText 입력 중 외부 터치 시 키보드 내리기
        val outer_layout = findViewById<ConstraintLayout>(R.id.layout_signUp4)
        outer_layout.setOnClickListener {
            hideKeyboard()
        }

        val stuNum = intent.getStringExtra("stuNum")
        val nickname = intent.getStringExtra("nickname")
        val selectedPhotoUri = intent.getStringExtra("selectedPhotoUri")
        val major = intent.getStringExtra("major")

        // MBTI, 관심사에 대한 spinner 세팅
        SpinnerSettings(findViewById(R.id.mbti_spinner_signup4), R.array.MBTI_type)
        SpinnerSettings(findViewById(R.id.topinterest_spinner_signup4), R.array.large_category)

        // 관심사1 - 대분류 스피너 설정
        val mainCategorySpinner = findViewById<Spinner>(R.id.topinterest_spinner_signup4)
        val mainSpinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.large_category,
            android.R.layout.simple_spinner_item
        )
        mainCategorySpinner.adapter = mainSpinnerAdapter // 어댑터 연결
        mainSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 관심사2 - 소분류 스피너 설정
        val subCategorySpinner = findViewById<Spinner>(R.id.underinterest_spinner_signup4)
        var subSpinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.sub_category_title,
            android.R.layout.simple_spinner_item
        )
        subCategorySpinner.adapter = subSpinnerAdapter
        subSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 스피너 동작 감지(다중 스피너)
        mainCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) { // 소분류
                    subSpinnerAdapter = ArrayAdapter.createFromResource(
                        this@SignUpActivity4,
                        R.array.sub_category_title,
                        android.R.layout.simple_spinner_item
                    )
                } else if (position == 1) {    //엔터테인먼트·예술
                    subSpinnerAdapter = ArrayAdapter.createFromResource(
                        this@SignUpActivity4,
                        R.array.sub_category_entertainment,
                        android.R.layout.simple_spinner_item
                    )
                } else if (position == 2) { //생활·노하우·쇼핑
                    subSpinnerAdapter = ArrayAdapter.createFromResource(
                        this@SignUpActivity4,
                        R.array.sub_category_dailyLife,
                        android.R.layout.simple_spinner_item
                    )
                } else if (position == 3) {  //취미·여가·여행
                    subSpinnerAdapter = ArrayAdapter.createFromResource(
                        this@SignUpActivity4,
                        R.array.sub_category_hobby,
                        android.R.layout.simple_spinner_item
                    )
                } else {    //지식·동향(4)
                    subSpinnerAdapter = ArrayAdapter.createFromResource(
                        this@SignUpActivity4,
                        R.array.sub_category_knowledge,
                        android.R.layout.simple_spinner_item
                    )
                }

                // 공통 기능
                subCategorySpinner.adapter = subSpinnerAdapter
                subSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                subCategorySpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
//                            Toast.makeText(applicationContext, "2번째 스피너 완료", Toast.LENGTH_SHORT)
//                                .show()

                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            Toast.makeText(applicationContext, "Nothing Selected", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(applicationContext, "Nothing Selected", Toast.LENGTH_SHORT).show()
            }

        }

        signup_button_signup4.setOnClickListener {
            var lecture = lecture1_edittext_signup4.text.toString()
            var mbti= mbti_spinner_signup4.selectedItem.toString()
            var interest = subCategorySpinner.selectedItem.toString()
            val statusmessage = statusmessage_edittext_signup4.text.toString()
            saveUserToFirebaseDatabase(stuNum!!, major!!, nickname!!, lecture, mbti, interest,statusmessage, selectedPhotoUri!!)
        }
    }



    // Firebase에 실제로 저장 (Users에 들어갈 것들)
    fun saveUserToFirebaseDatabase(stuNum:String, major:String, nickname:String, lecture:String, mbti:String, interest:String, statusmessage:String, profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: "" // default 체크
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")

        val user = Users(uid, stuNum, nickname,
            major,lecture,mbti,interest,statusmessage, profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("SignUpActivity4","we saved the user's image,name,major to Firebase Database")

                // 회원가입 후 새로 메세지 액티비티로 연결
                val intent = Intent(this, MyPageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
    }

    // 이전 화면으로 되돌리기 구현
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    //키보드 숨기기
    fun hideKeyboard() {
        val editText1 = findViewById<EditText>(R.id.lecture1_edittext_signup4)
        val editText2 = findViewById<EditText>(R.id.statusmessage_edittext_signup4)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText1.windowToken, 0)
        imm.hideSoftInputFromWindow(editText2.windowToken, 0)
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