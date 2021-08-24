package com.example.sujungmate.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sujungmate.R
import com.example.sujungmate.tables.Request
import com.squareup.picasso.Picasso

class ReceiveRequestAdapter(private val reqList : ArrayList<Request>) : RecyclerView.Adapter<ReceiveRequestAdapter.MyViewHolder3>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder3 {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.receive_request_item,
            parent, false
        )
        return MyViewHolder3(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder3, position: Int) {
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

    class MyViewHolder3(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val PROFILE: ImageView = itemView.findViewById(R.id.iv_PROFILE3_receive)
        val NAME: TextView = itemView.findViewById(R.id.tv_NICKNAME_receive)
        val MAJORandSTUNUM: TextView = itemView.findViewById(R.id.tv_STUNUMandMAJOR_receive)
        val MESSAGE: TextView = itemView.findViewById(R.id.tv_MESSAGE_receive)
        val ACCEPT_BT: Button = itemView.findViewById(R.id.bt_ACCEPT_receive)
        val DECLINE_BT: Button = itemView.findViewById(R.id.bt_DECLINE_receive)
    }
}