package com.example.learn1

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class ButtonAdapter(val items: List<DataButtons>,val listener: buttonClickListener ) : RecyclerView.Adapter<ButtonViewHolder>(){
    val a =0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items_buttons, parent, false)
        return ButtonViewHolder(view)
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        val current = items[position]
        holder.titleButton.text= current.name
        holder.titleButton.setOnClickListener {
            listener.onClick(current)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val titleButton : Button = itemView.findViewById(R.id.queryButton)
}

interface buttonClickListener{
    fun onClick( dataButtons: DataButtons)
}