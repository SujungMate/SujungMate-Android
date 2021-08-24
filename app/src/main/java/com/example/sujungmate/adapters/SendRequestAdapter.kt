package com.example.sujungmate.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sujungmate.R
import com.example.sujungmate.tables.Request
import com.squareup.picasso.Picasso

class SendRequestAdapter(private val reqList : ArrayList<Request>) : RecyclerView.Adapter<SendRequestAdapter.MyViewHolder2>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder2 {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.send_request_item,
            parent, false
        )
        return MyViewHolder2(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder2, position: Int) {
        val currentItem = reqList[position]

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
        return reqList.size
    }

    class MyViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val PROFILE: ImageView = itemView.findViewById(R.id.iv_PROFILE2_mateManage)
        val NAME: TextView = itemView.findViewById(R.id.tv_NAME2_mateManage)
        val MAJORandSTUNUM: TextView = itemView.findViewById(R.id.tv_MAJORandSTUNUM2_mateManage)
        val MESSAGE: TextView = itemView.findViewById(R.id.tv_MESSAGE2_mateManage)
    }


}