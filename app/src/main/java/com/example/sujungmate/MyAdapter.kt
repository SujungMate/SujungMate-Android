package com.example.sujungmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sujungmate.tables.Users

class MyAdapter(private val userList : ArrayList<Users>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private val limit : Int = 4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_item,
            parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList[position]

        //holder.PROFILE.text = currentItem.profile_img.toString()
        holder.NAME.text = currentItem.nickname
        holder.MAJOR.text = currentItem.major
        holder.MESSAGE.text = currentItem.msg
    }

    override fun getItemCount(): Int {
        if(userList.size > limit)
            return limit //limit 개수만 가져오기
        else
            return userList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        // val PROFILE : TextView = itemView.findViewById(R.id.tv_PROFILE)
        val NAME : TextView = itemView.findViewById(R.id.tv_NAME)
        val MAJOR : TextView = itemView.findViewById(R.id.tv_MAJOR)
        val MESSAGE : TextView = itemView.findViewById(R.id.tv_MESSAGE)
    }
}