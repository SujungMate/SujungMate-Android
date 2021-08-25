package com.example.sujungmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.sujungmate.messages.ChatActivity
import com.example.sujungmate.messages.ChatManageActivity
import com.example.sujungmate.messages.NewMessagesActivity
import com.example.sujungmate.messages.UserItem
import com.example.sujungmate.tables.ChatMessage
import com.example.sujungmate.tables.SujungMateSend
import com.example.sujungmate.tables.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.auth.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_manage.*
import kotlinx.android.synthetic.main.activity_my_page.*
import kotlinx.android.synthetic.main.activity_new_messages.*
import kotlinx.android.synthetic.main.latest_message_row.view.*
import kotlinx.android.synthetic.main.sujungmate_mypage.view.*
import kotlinx.android.synthetic.main.user_row_new_messages.view.*

class MyPageActivity : AppCompatActivity() {
    companion object {
        var currentUser: Users? = null
    }

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        recyclerview_mypage.adapter = adapter
        recyclerview_mypage.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        // 내 정보 수정 버튼 레이어 순서 올리기
        modifyBtn_myPage.bringToFront()
        modifyBtn_myPage.setOnClickListener {
            val intent = Intent(this, InfoModifyActivity::class.java)
            startActivity(intent)
        }

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

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(Users::class.java)
                Log.d("LatestMessages","Current user ${currentUser}?.username")
                nickname_myPage.text = currentUser?.nickname
                major_myPage.text = currentUser?.major
                studentID_myPage.text = currentUser?.stuNum?.slice(IntRange(2,3)) + "학번"
                statusMessage_myPage.text = currentUser?.msg
                Picasso.get().load(currentUser!!.profile_img).into(profileImage_myPage)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        listenForUserInfo()
    }


    // 사용자 정보 실시간 적용
    private fun listenForUserInfo(){
        val userId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$userId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            }

            // 내 정보 수정 시 이벤트 리스너 적용
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("ChildChanged", "changing detected")
                // 회원 정보 적용
                for (snapshot in snapshot.children) {
                    val user = snapshot.getValue(Users::class.java) ?: return
                    ref.setValue(user)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}

/*
class SujungMateRow(val sujungMateSend: SujungMateSend): Item<ViewHolder>() {
    var chatPartnerUser: Users? = null
    override fun bind(viewHolder: ViewHolder, position: Int) {
        // 메세지 텍스트뷰를 가장 최근 메세지가 보이도록 연결
        //viewHolder.itemView.message_textview_latest_message.text = chatMessage.text

        // 최근 메세지에 맞는 유저 이름 보이게
        val chatPartnerId: String
        if (sujungMateSend.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = sujungMateSend.toId
        } else {
            chatPartnerId = sujungMateSend.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        viewHolder.itemView.sujungMate_nickname_mypage.text = ""
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(Users::class.java)
                viewHolder.itemView.sujungMate_nickname_mypage.text =
                    chatPartnerUser?.nickname
                viewHolder.itemView.sujungMate_major_mypage.text =
                    chatPartnerUser?.major + " " + chatPartnerUser?.stuNum?.slice(
                        IntRange(2, 3)) + "학번"

                /*
                // 유저에 맞는 이미지 사진
                val targetImageView = viewHolder.itemView.imageview_latest_message
                Picasso.get().load(chatPartnerUser?.profile_img).into(targetImageView)*/
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.sujungmate_mypage
    }
}
 */
