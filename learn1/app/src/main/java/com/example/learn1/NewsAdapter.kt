package com.example.learn1

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions


class NewsAdapter(val items: List<DataNews>) : RecyclerView.Adapter<NewsViewHolder>() {
    init {

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items_news, parent, false)
        return NewsViewHolder(view)

    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val current = items[position]
        holder.imageview.setOnClickListener{
            Log.d("cardv", current.author?:"null")
        }

        holder.bookbut.setOnClickListener{
            current.saved = !current.saved
            updateButton(current.saved,holder.bookbut)
        }
        holder.titleview.text = current.title
        holder.authorTextView.text = current.author
        Glide.with(holder.itemView.context)
            .load(current.urlToImage)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(50)))
            .into(holder.imageview)
    }

    override fun getItemCount(): Int {
        return items.size
    }
    fun updateButton(saved:Boolean, bookbut:ImageButton){
        val background = if(saved) R.drawable.bookmark_saved else R.drawable.bookmark_not_saved
            bookbut.setBackgroundResource(background)
    }

}

class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val titleview: TextView = itemView.findViewById<TextView>(R.id.title)
    val authorTextView: TextView = itemView.findViewById<TextView>(R.id.authorview)
    val imageview: ImageView = itemView.findViewById<ImageView>(R.id.newsImage)
    val bookbut: ImageButton = itemView.findViewById<ImageButton>(R.id.bookmarkButton)
}
