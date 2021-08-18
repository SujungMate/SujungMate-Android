package com.example.sujungmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up2.*
import kotlinx.android.synthetic.main.activity_sign_up2_1.*

// 성신 이메일 유효성 인증
class SignUpActivity2_1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up2_1)

        // 뒤로가기 버튼
        val toolbar = findViewById<Toolbar>(R.id.toolbar_signup2_1)
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)

        /*
        next_button_signup2_1.setOnClickListener{

            sendEmailCheck()
        }

         */
    }

    /*
    private fun sendEmailCheck() {
        val usertask = FirebaseAuth.getInstance().currentUser!!.reload()

        usertask.addOnSuccessListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user!!.isEmailVerified) { // 인증 성공
                Toast.makeText(this, "이메일 인증되었습니다.", Toast.LENGTH_SHORT).show()
                val stuNum = intent.getStringExtra("stuNum")
                // signup3 액티비티로 이동
                val intent = Intent(this, SignUpActivity3::class.java)
                intent.putExtra("stuNum",stuNum)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            } else { // 인증 실패
                Toast.makeText(this, "이메일 인증이 실패하였습니다. 재시도 해주세요.", Toast.LENGTH_SHORT).show()
                // 이메일 인증 실패 시, Firebase에서 삭제
                FirebaseAuth.getInstance().currentUser!!.delete()
                val intent = Intent(this, SignUpActivity2::class.java)

                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
        }


    }
     */

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
}