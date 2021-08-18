package com.example.sujungmate.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.sujungmate.R
import com.example.sujungmate.tables.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_messages.*
import kotlinx.android.synthetic.main.user_row_new_messages.view.*

class NewMessagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_messages)

        // 액션바 title 변경
        supportActionBar?.title = "Select User"

        // GroupAdater 이용해서 사용자 추가
/*        val adapter = GroupAdapter<ViewHolder>()
        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())

        recyclerview_newmessage.adapter = adapter*/

        fetchUsers()
    }

    // intent.putExtra(USER_KEY,userItem.user.username) 할 때 필요한 유저키
    companion object {
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers(){
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach{
                    // key, value(uid), profileImageUrl, username 가져오는거 확인 가능
                    Log.d("NewMessage", it.toString())
                    val user = it.getValue(Users::class.java)
                    // 내가 데이터베이스에 넣은 유저들 보이는 것
                    // 추후에 매칭된 유저들을 데이터베이스에 넣으면 될 듯
                    if(user!=null) {
                        adapter.add(UserItem(user))
                    }
                }

                adapter.setOnItemClickListener{
                        item,view ->
                    val userItem = item as UserItem

                    // view 사용해서 Chat Log Activity 연결
                    val intent = Intent(view.context, ChatActivity::class.java)
                    // 아래 item은 UserItem을 의미해서 유저 이름 건네주기
                    // intent.putExtra(USER_KEY,userItem.user.username)
                    intent.putExtra(USER_KEY,userItem.user) // User 클래스에 Parcelable 추가하고 @Parcelize 어노테이션 추가해서 사용가능

                    startActivity(intent)

                    // finish로 ChatLog 나갈시 Latetest로 돌아가게 함
                    finish()
                }
                recyclerview_newmessage.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {


            }

        })
    }
}

class UserItem(val user: Users): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        // 유저 이름 텍스트 보이기
        viewHolder.itemView.username_textview_new_message.text = user.nickname
        viewHolder.itemView.major_textview_newmessage.text = user.major
        // 유저 사진 보이기
        Picasso.get().load(user.profile_img).into(viewHolder.itemView.imageview_new_message)
    }

    // new message에서 사용자 하나의 레이아웃
    // recycle로 계속 불러옴
    override fun getLayout(): Int {
        return R.layout.user_row_new_messages
    }
}