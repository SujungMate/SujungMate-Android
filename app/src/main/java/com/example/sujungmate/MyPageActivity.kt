package com.example.sujungmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.sujungmate.messages.ChatManageActivity
import com.example.sujungmate.tables.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_manage.*
import kotlinx.android.synthetic.main.activity_my_page.*
import kotlinx.android.synthetic.main.user_row_new_messages.view.*

class MyPageActivity : AppCompatActivity() {
    companion object {
        var currentUser: Users? = null
    }

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
    }
}

private fun fetchCurrentUser(){

}
