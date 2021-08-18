package com.example.sujungmate

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.sujungmate.messages.ChatManageActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 키보드 내리기
        val outer_layout = findViewById<ConstraintLayout>(R.id.layout_login) // 레이아웃 가져오기
        outer_layout.setOnClickListener {
            hideKeyboard()
        }

        signup_button_login.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        // binding으로 findViewById 없애버림
        login_button_login.setOnClickListener {
            val email = schoolemail_edittext_login.text.toString() + "@sungshin.ac.kr"
            val password = password_edittext_login.text.toString()

            Log.d("Login", "Attempt login with email/pw: $email/***")

            if (email.length == 0 || password.length == 0) {
                Toast.makeText(this, "이메일, 비밀번호를 반드시 입력하세요", Toast.LENGTH_SHORT).show()
            } else {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MyPageActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "로그인 실패. 다시 시도해보세요.", Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(this, LoginActivity::class.java)
                            // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                // .add

            }
        }

        /*
        val back_to_register_text_view = findViewById<TextView>(R.id.back_to_register_text_view)
        back_to_register_text_view.setOnClickListener {
            finish()
        }
        */
    }

    //키보드 숨기기
    fun hideKeyboard() {
        val editText1 = findViewById<EditText>(R.id.schoolemail_edittext_login)
        val editText2 = findViewById<EditText>(R.id.password_edittext_login)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText1.windowToken, 0)
        imm.hideSoftInputFromWindow(editText2.windowToken, 0)
    }
}