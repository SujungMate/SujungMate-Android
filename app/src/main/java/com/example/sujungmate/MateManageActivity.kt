package com.example.sujungmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sujungmate.adapters.ReceiveRequestAdapter
import com.example.sujungmate.adapters.SendRequestAdapter
import com.example.sujungmate.messages.ChatManageActivity
import com.example.sujungmate.tables.Request
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat_manage.*
import kotlinx.android.synthetic.main.activity_mate_manage.*

class MateManageActivity : AppCompatActivity() {
    val sendReqArrayList : ArrayList<Request> = ArrayList()
    val receiveReqArrayList : ArrayList<Request> = ArrayList()
    val myUid = FirebaseAuth.getInstance().uid  // 현재 로그인 한 정보
    val sendRef = FirebaseDatabase.getInstance().getReference("/request/send/$myUid")
    val receiveRef = FirebaseDatabase.getInstance().getReference("request/receive/$myUid")

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

        /* 보낸 요청 읽어오기 */
        sendRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for(sendReq in snapshot.children) {
                        val req = sendReq.getValue(Request::class.java)
                        sendReqArrayList.add(req!!)
                    }

                }
                // 보낸 요청 RecyclerView 띄우기
                sendRequestListRV.layoutManager = LinearLayoutManager(this@MateManageActivity)
                sendRequestListRV.setHasFixedSize(true)
                sendRequestListRV.adapter = SendRequestAdapter(sendReqArrayList)

            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }

        })

        /* 받은 요청 읽어오기 */
        receiveRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for(receiveReq in snapshot.children){
                        val req = receiveReq.getValue(Request::class.java)
                        receiveReqArrayList.add(req!!)
                    }
                }

                // 받은 요청 RecyclerView 띄우기
                receiveRequestListRV.layoutManager = LinearLayoutManager(this@MateManageActivity)
                receiveRequestListRV.setHasFixedSize(true)
                receiveRequestListRV.adapter = ReceiveRequestAdapter(receiveReqArrayList)
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })

        // 매칭 유형 선택으로
        goMateType.setOnClickListener {
            val intent = Intent(this, MateTypeActivity::class.java)
            startActivity(intent)
        }
    }
}

