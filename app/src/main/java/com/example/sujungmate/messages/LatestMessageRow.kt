package com.example.sujungmate.messages

import com.example.sujungmate.R
import com.example.sujungmate.tables.ChatMessage
import com.example.sujungmate.tables.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>(){
    var chatPartnerUser: Users? = null
    override fun bind(viewHolder: ViewHolder, position: Int) {
        // 메세지 텍스트뷰를 가장 최근 메세지가 보이도록 연결
        viewHolder.itemView.message_textview_latest_message.text = chatMessage.text

        // 최근 메세지에 맞는 유저 이름 보이게
        val chatPartnerId:String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        viewHolder.itemView.username_textview_latest_message.text = ""
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(Users::class.java)
                viewHolder.itemView.username_textview_latest_message.text = chatPartnerUser?.nickname
                viewHolder.itemView.major_stunum_textview_latest_message.text = chatPartnerUser?.major

                // 유저에 맞는 이미지 사진
                val targetImageView = viewHolder.itemView.imageview_latest_message
                Picasso.get().load(chatPartnerUser?.profile_img).into(targetImageView)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
    /*



    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.message_textview_latest_message.text = chatMessage.text



        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")

    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }*/
}