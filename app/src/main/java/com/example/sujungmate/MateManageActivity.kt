package com.example.sujungmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sujungmate.messages.ChatManageActivity
import com.example.sujungmate.tables.Request
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat_manage.*
import kotlinx.android.synthetic.main.activity_mate_manage.*

class MateManageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mate_manage)

        // 바텀 버튼
        myPage_mateManage.setOnClickListener{
            val intent = Intent(this, MyPageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        chatRoom_mateManage.setOnClickListener{
            val intent = Intent(this, ChatManageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
//        mateBtn_chatmanage.setOnClickListener{
//            val intent = Intent(this, MateManageActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//        }

        //변수 선언
        var sendReqArrayList : ArrayList<Request> = ArrayList<Request>()

        // 보낸 요청 읽어오기
        val dbref = FirebaseDatabase
            .getInstance()
            .getReference("/request/send/${FirebaseAuth.getInstance().currentUser!!.uid}")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for(sendReq in snapshot.children) {
                        val req = sendReq.getValue(Request::class.java)
                        sendReqArrayList.add(req!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }

        })

        // 보낸 요청 RecyclerView 띄우기
        val sendReqRecyclerView : RecyclerView = findViewById(R.id.sendRequestListRV)
        sendReqRecyclerView.layoutManager = LinearLayoutManager(this@MateManageActivity)
        sendReqRecyclerView.setHasFixedSize(true)
        sendReqRecyclerView.adapter = SendRequestAdapter(sendReqArrayList)

        // 매칭 관리로
        findViewById<View>(R.id.goMateType).setOnClickListener {
            val intent = Intent(this, MateTypeActivity::class.java)
            startActivity(intent)
        }
    }
}

