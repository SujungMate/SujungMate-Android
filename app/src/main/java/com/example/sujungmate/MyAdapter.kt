package com.example.sujungmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sujungmate.tables.Users
import com.squareup.picasso.Picasso

class MyAdapter(private val userList: ArrayList<Users>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private val limit: Int = 4
    private lateinit var mlistener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mlistener = listener
    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.user_item,
            parent, false
        )
        return MyViewHolder(itemView, mlistener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList[position]

        Picasso.get().load(currentItem.profile_img).into(holder.PROFILE)
        holder.NAME.text = currentItem.nickname
        holder.MAJORandSTUNUM.text = holder.itemView.context.getString(
            R.string.MAJORandSTUNUM,
            currentItem.major,
            currentItem.stuNum.slice(IntRange(2, 3)).toInt()
        )
        holder.MESSAGE.text = currentItem.msg
    }

    override fun getItemCount(): Int {
        if (userList.size > limit)
            return limit //limit 개수만 가져오기
        else
            return userList.size
    }

    class MyViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val PROFILE: ImageView = itemView.findViewById(R.id.iv_PROFILE)
        val NAME: TextView = itemView.findViewById(R.id.tv_NAME)
        val MAJORandSTUNUM: TextView = itemView.findViewById(R.id.tv_MAJORandSTUNUM)
        val MESSAGE: TextView = itemView.findViewById(R.id.tv_MESSAGE)
        val REQUEST: Button = itemView.findViewById(R.id.bt_REQUEST)

        // 요청 버튼 클릭 리스너
        init {
            REQUEST.setOnClickListener {
                listener.onItemClick(adapterPosition)
                REQUEST.isEnabled = false
                REQUEST.setBackgroundResource(R.drawable.button_deactivation)
            }
        }

    }

}