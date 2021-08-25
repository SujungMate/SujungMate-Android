package com.example.sujungmate.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.sujungmate.LoginActivity
import com.example.sujungmate.MateManageActivity
import com.example.sujungmate.R
import com.example.sujungmate.tables.ChatMessage
import com.example.sujungmate.tables.Request
import com.example.sujungmate.tables.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.auth.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatActivity : AppCompatActivity() {
    companion object{
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>()

    var toUser: Users? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recyclerview_chat_log.adapter = adapter

        supportActionBar?.title = "Chat Log"


        // NewMessagesActivity에서 가져왔던 유저키로 유저네임 가져오기
        //val username = intent.getStringExtra(NewMessagesActivity.USER_KEY)
        // Parcelable 이용
        toUser = intent.getParcelableExtra<Users>(MateManageActivity.USER_KEY)

        // username으로 액션바 타이틀 변경하기
        supportActionBar?.title = toUser?.nickname

        // 채팅창 tool바 이름, 전공
        toolbar_chat_name.text = toUser?.nickname
        toolbar_chat_major_stunum.text = toUser?.major + " " + toUser?.stuNum?.slice(IntRange(2,3)) + "학번"
        // 뒤로가기
        setSupportActionBar(toolbar_chat)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)

        // 채팅 돌기 테스트 (xml 상에서 넣은 text 확인)
        //setUpDummyData()

        // 실제 채팅 보이기
        listenForMessages()

        send_button_chat_log.setOnClickListener{
            // SEND 버튼 클릭시 TAG 확인 가능
            Log.d(TAG,"Attempt to send message...")
            performSendMessage()
        }
    }

    // 액션바일때의 옵션
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            // 짝수정 요청
            R.id.menu_sujungmate -> {

            }
            // 신고하기
            R.id.menu_report -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun listenForMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                // null 오류 발생시 앱 종료되므로 ChatMessage 클래스에 constructor 작성
                // adapter 전역변수로 선언
                if(chatMessage!=null){
                    Log.d(TAG, chatMessage!!.text)

                    // 여기서 toId는 보낸 사람 uid
                    if(chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = ChatManageActivity.currentUser ?: return
                        // 왼쪽 (메세지 받기)
                        adapter.add(ChatFromItem(chatMessage.text, currentUser!!))
                    }else {
                        // 오른쪽 (메세지 보내기)
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }
                }

                recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)
            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

        })
    }

    private fun performSendMessage(){
        // 어떻게 파이어베이스에 메세지를 보내는지 구현
        val text = edittext_chat_log.text.toString()

        // 보낸 사람 uid
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<Users>(MateManageActivity.USER_KEY)
        // 받은 사람 uid
        val toId = user!!.uid

        if(fromId == null) return

        // 파이어베이스 경로에 메세지 넣기
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis()/1000)
        reference.setValue(chatMessage).addOnSuccessListener {
            Log.d(TAG,"Saved our chat message: ${reference.key}")
            // 입력 창 깨끗하게
            edittext_chat_log.text.clear()
            // 채팅 스크롤
            recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
        }

        toReference.setValue(chatMessage)

        // 경로를 정해서 최근 메세지를 파이어베이스에 넣기
        // 내꺼랑 상대꺼랑 둘다 넣어주기
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }

    /*
    private fun setUpDummyData(){
        // 채팅이 사이클 돌면서 보이게
        val adapter = GroupAdapter<ViewHolder>()
        adapter.add(ChatFromItem("From Message"))
        adapter.add(ChatToItem("To Message\nTo Message"))

        recyclerview_chat_log.adapter = adapter
    } */
}

// UserItem 처럼
class ChatFromItem(val text: String, val user:Users): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        // 받은 메세지 연결
        viewHolder.itemView.textview_from_row.text = text

        // 유저 이미지 보이기
        val uri = user.profile_img
        val targetImageView = viewHolder.itemView.imageview_chat_from_row
        Picasso.get().load(uri).into(targetImageView)

    }

    // 텍스트 레이아웃(보낸거)
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text: String, val user:Users): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        // 보낸 메세지 연결
        viewHolder.itemView.textview_to_row.text = text

        // 유저 이미지 보이기
        val uri = user.profile_img
        val targetImageView = viewHolder.itemView.imageview_chat_to_row
        Picasso.get().load(uri).into(targetImageView)
    }

    // 텍스트 레이아웃(받는거)
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}