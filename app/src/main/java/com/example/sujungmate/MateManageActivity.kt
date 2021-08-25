package com.example.sujungmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sujungmate.adapters.SendRequestAdapter
import com.example.sujungmate.messages.ChatActivity
import com.example.sujungmate.messages.ChatManageActivity
import com.example.sujungmate.tables.Request
import com.example.sujungmate.tables.Users
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
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

    // intent.putExtra(USER_KEY, ~) 할 때 필요한 유저키
    companion object {
        val USER_KEY = "USER_KEY"
    }

    // 받은요청 리사이클러뷰 어댑터
    class ReceiveRequestAdapter(private val reqList : ArrayList<Request>)
        : RecyclerView.Adapter<ReceiveRequestAdapter.MyViewHolder3>() {
        val myUid = FirebaseAuth.getInstance().uid  // 현재 로그인 한 정보
        val userDB = FirebaseDatabase.getInstance().getReference("users")
        val receiveRef = FirebaseDatabase.getInstance().getReference("request/receive/$myUid")


        interface Callback {
            fun success(snapshot : DataSnapshot)
//            fun failed(errorMessage : String)
        }

        fun getUserData(otherUid : String, callback: Callback) {
            userDB.child("$otherUid").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback.success(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    error.toException().printStackTrace().toString()
                }
            })

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder3 {
            val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.receive_request_item,
                parent, false
            )
            return MyViewHolder3(itemView)

        }

        override fun onBindViewHolder(holder: MyViewHolder3, position: Int) {
            // currentItem은 상대방에 대한 정보
            val currentItem = reqList[position] // currentItem : Request

            Picasso.get().load(currentItem.profile_img).into(holder.PROFILE)
            holder.NAME.text = currentItem.nickname
            holder.MAJORandSTUNUM.text = holder.itemView.context.getString(
                R.string.MAJORandSTUNUM,
                currentItem.major,
                currentItem.stuNum.slice(IntRange(2, 3)).toInt()
            )
            holder.MESSAGE.text = currentItem.msg

            // 수락 버튼 클릭 이벤트(NewMessages 액티비티에 유저 값 전달)
            holder.ACCEPT_BT.setOnClickListener { v ->
                holder.ACCEPT_BT.isEnabled = false
                holder.ACCEPT_BT.setBackgroundResource(R.drawable.button_deactivation)

                // 받은 요청 데이터 삭제
                receiveRef.child(currentItem.uid).removeValue()

                var otherUser : Users? = null
                getUserData(currentItem.uid, object : Callback {
                    override fun success(snapshot: DataSnapshot) {
                        otherUser = snapshot.getValue(Users::class.java)!!
                        val intent = Intent(v.context, ChatActivity::class.java)
                        intent.putExtra(USER_KEY, otherUser)
                        v.context.startActivity(intent) // 수락 클릭하자마자 ChatActivity로 이동
                    }
                })

            }

            // 거절 버튼 클릭 이벤트
            holder.DECLINE_BT.setOnClickListener {
                holder.DECLINE_BT.isEnabled = false
                holder.DECLINE_BT.setBackgroundResource(R.drawable.button_deactivation)
            }

        }

        override fun getItemCount(): Int {
            return reqList.size
        }

        class MyViewHolder3(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val PROFILE: ImageView = itemView.findViewById(R.id.iv_PROFILE3_receive)
            val NAME: TextView = itemView.findViewById(R.id.tv_NICKNAME_receive)
            val MAJORandSTUNUM: TextView = itemView.findViewById(R.id.tv_STUNUMandMAJOR_receive)
            val MESSAGE: TextView = itemView.findViewById(R.id.tv_MESSAGE_receive)
            val ACCEPT_BT: Button = itemView.findViewById(R.id.bt_ACCEPT_receive)
            val DECLINE_BT: Button = itemView.findViewById(R.id.bt_DECLINE_receive)

        }
    }
}

