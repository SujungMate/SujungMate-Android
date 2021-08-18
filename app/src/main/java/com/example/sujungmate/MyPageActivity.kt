package com.example.sujungmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sujungmate.messages.ChatManageActivity
import kotlinx.android.synthetic.main.activity_chat_manage.*
import kotlinx.android.synthetic.main.activity_my_page.*

class MyPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        // 바텀 버튼
        /*
        myPageBtn_myPage.setOnClickListener{
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }*/
        chatRoomBtn_myPage.setOnClickListener{
            val intent = Intent(this, ChatManageActivity::class.java)
            startActivity(intent)
        }
        mateBtn_myPage.setOnClickListener{
            val intent = Intent(this, MateManageActivity::class.java)
            startActivity(intent)
        }
    }
}