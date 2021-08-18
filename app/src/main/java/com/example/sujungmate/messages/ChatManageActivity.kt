package com.example.sujungmate.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.sujungmate.LoginActivity
import com.example.sujungmate.MyPageActivity
import com.example.sujungmate.R
import com.example.sujungmate.SignUpActivity4
import com.example.sujungmate.tables.ChatMessage
import com.example.sujungmate.tables.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_manage.*

// LatestMessagesActivity
class ChatManageActivity : AppCompatActivity() {
    companion object {
        var currentUser: Users? = null
        val TAG = "LatestMessages"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_manage)
        recyclerview_chat_manage.adapter = adapter
        // 최근 사용자를 vertical로 나눠서 밑에 선 생기게 꾸밈
        // 레이아웃 꾸미는 걸로 변경함
        recyclerview_chat_manage.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        // 바텀네비바 옵션
        bottom_navbar.setOnNavigationItemReselectedListener { item ->
            when (item?.itemId) {
                // 새메세지
                // manifest.xml에 parent_activity를 추가해서 뒤로가기 자동으로 생성
                R.id.menu_new_message -> {
                    val intent = Intent(this, NewMessagesActivity::class.java)
                    startActivity(intent)
                }
                // 로그아웃
                R.id.menu_sign_out -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                R.id.menu_my_page -> {
                    val intent = Intent(this, MyPageActivity::class.java)
                    startActivity(intent)
                }
            }
        }



        // set item click listener on your adapter
        // 아이템 클릭시 이동
        adapter.setOnItemClickListener{item,view->
            Log.d(TAG,"123")
            val intent = Intent(this, ChatActivity::class.java)

            // we are missing the chat partner user
            // 사용자에 맞는 채팅창 보이도록 연결 (LatestMessageRow에서 User을 chatPartnerUser로 따로 뽑아냄)
            val row = item as LatestMessageRow
            intent.putExtra(NewMessagesActivity.USER_KEY,row.chatPartnerUser)
            startActivity(intent)
        }

        // setupDummyRows()

        listenForLatestMessages()

        fetchCurrentUser()

        verifyUserIsLoggedIn()
    }


    // hashMap으로 최근 메세지 내용 key, value값 맞춰서 보여주게하기
    val latestMessagesMap = HashMap<String, ChatMessage>()

    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        latestMessagesMap.values.forEach{
            adapter.add(LatestMessageRow(it))
        }
    }


    // 가장 최근 메세지 보이기
    private fun listenForLatestMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener {

            // 메세지 집어넣기
            // Hash 맵으로 작성해야 각 유저에 맞는 최근 메세지가 적용(아니면 그냥 계속 쌓임)
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            // 2개의 계정이 메세지를 주고 받을 때 최근 메세지가 '바로바로!' 변경되도록 Changed 코드 작성
            // Hash 맵으로 작성해야 각 유저에 맞는 최근 메세지가 적용(아니면 그냥 계속 쌓임)
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // 메세지 내용
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildRemoved(p0: DataSnapshot) {
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    val adapter = GroupAdapter<ViewHolder>()


    //private fun setupDummyRows(){
    //    adapter.add(LatestMessageRow())
    //    adapter.add(LatestMessageRow())
    //}


    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(Users::class.java)
                Log.d("LatestMessages","Current user $currentUser?.username")
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null){
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    // 액션바일때의 옵션
    /*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            // 새메세지
            // manifest.xml에 parent_activity를 추가해서 뒤로가기 자동으로 생성
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessagesActivity::class.java)
                startActivity(intent)
            }
            // 로그아웃
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.menu_my_page -> {
                val intent = Intent(this, MyPageActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
     */
}