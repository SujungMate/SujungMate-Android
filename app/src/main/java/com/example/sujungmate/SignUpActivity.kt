package com.example.sujungmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // 이용약관을 동의해야만 다음 회원가입 창으로 넘어가기
        next_button_signup.setOnClickListener{
            if(agree_button_signup.isChecked) {
                val intent = Intent(this, SignUpActivity2::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this,"이용약관에 동의하시길 바랍니다.",Toast.LENGTH_SHORT).show()
            }
        }

    }
}