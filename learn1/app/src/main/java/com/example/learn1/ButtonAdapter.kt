package com.example.learn1

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.widget.Button
import android.widget.ImageButton
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
        holder.deleteButton.setOnClickListener{
            deleteAnimation(holder.deleteButton)
            deleteAnimation(holder.titleButton)?.withEndAction{
                listener.deleteButton(current)
                returnAnimation(holder.deleteButton)
                returnAnimation(holder.titleButton)
            }

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
    fun deleteAnimation(widget: View): ViewPropertyAnimator? {
        return widget.animate().apply {
            duration= 500
            translationX(1500f)
            alpha(0.5f)
        }
    }
    fun returnAnimation(widget: View): ViewPropertyAnimator? {
        return widget.animate().apply {
            duration= 1
            translationX(0f)
            alpha(1f)
        }
    }

}

class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val titleButton : Button = itemView.findViewById(R.id.queryButton)
    val deleteButton : ImageButton = itemView.findViewById(R.id.delete_query)
}

interface buttonClickListener{
    fun onClick( dataButtons: DataButtons)
    fun deleteButton( dataButtons: DataButtons)
}