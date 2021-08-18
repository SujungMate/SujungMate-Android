package com.example.sujungmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sujungmate.messages.ChatManageActivity
import kotlinx.android.synthetic.main.activity_chat_manage.*
import kotlinx.android.synthetic.main.activity_chat_manage.myPageBtn_chatmanage
import kotlinx.android.synthetic.main.activity_mate_manage.*

class MateManageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mate_manage)

        // 바텀 버튼
        myPage_mateManage.setOnClickListener{
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }

        chatRoom_mateManage.setOnClickListener{
            val intent = Intent(this, ChatManageActivity::class.java)
            startActivity(intent)
        }
        /*
        Mate_mateManage.setOnClickListener{
            val intent = Intent(this, MateManageActivity::class.java)
            startActivity(intent)
        }*/
    }
}