package com.example.sujungmate

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_sign_up2.*
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignUpActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up2)

        // 뒤로가기 버튼
        val toolbar = findViewById<Toolbar>(R.id.toolbar_signup2)
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)

        // EditText 입력 중 외부 터치 시 키보드 내리기
        val outer_layout = findViewById<ConstraintLayout>(R.id.layout_signup2) // 레이아웃 가져오기
        outer_layout.setOnClickListener {
            hideKeyboard()
        }

        // 이메일 인증 버튼 클릭시 유저 생성
        email_button_signup2.setOnClickListener{
            performRegister()
        }


        /*
        // 채연이 코드
        next_button_signup2.setOnClickListener {

            // 학번 ??? not null 확인 & 인증번호 확인 ???
            //var STUDENTID : String = findViewById<EditText>(R.id.schoolemail_edittext_signup2).text.toString().trim()

            val intent = Intent(this, SignUpActivity3::class.java)
            // SignUpActivity4로 학번 전달
            //intent.putExtra("subStudentID", STUDENTID.toLong())
            startActivity(intent)
        }
         */
    }

    private fun performRegister(){
        val stuNum = schoolemail_edittext_signup2.text.toString()

        val email = stuNum +"@sungshin.ac.kr"
        val password = password_edittext_signup2.text.toString()
        val passswordconfirm = passwordconfirm_edittext_signup2.text.toString()

        // email or password empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email/pw", Toast.LENGTH_SHORT).show()
            return
        }
        // 비밀번호가 일치하는지 안하는지
        if(!password.equals(passswordconfirm)){
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("MainActivity","Email is:"+email)
        Log.d("MainActivity","Password: $password")

        // Firebase 반영
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                // else if successful

                Log.d("Main", "Successfully created user with uid: ${it.result?.user?.uid}")
                //uploadImagetoFirebaseStorage()
                // 이메일 유효성 확인
                //sendEmailVerification()

                // 유저에게 이메일 보내기
                FirebaseAuth.getInstance().currentUser!!.sendEmailVerification().addOnCompleteListener { task ->
                    if (task.isSuccessful) { // 이메일 전송 성공
                        Toast.makeText(this,"이메일 확인 메일을 전송했습니다.",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, SignUpActivity2_1::class.java)
                        intent.putExtra("stuNum",stuNum)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    } else { // 이메일 전송 실패
                        Toast.makeText(this,"이메일 전송을 실패했습니다.",Toast.LENGTH_SHORT).show()
                    }
                }

            }
            .addOnFailureListener{
                // 회원가입 실패 원인 메세지 확인 가능 ex) 이메일이 온전치 못할 때
                Log.d("Main","Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
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
        val editText1 = findViewById<EditText>(R.id.schoolemail_edittext_signup2)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText1.windowToken, 0)
    }
}