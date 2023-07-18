package com.example.learn1

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
import com.example.learn1.DataClass.DataNews


class NewsAdapter(val items: List<DataNews>, val View:Int, val listener: newsClickListener) : RecyclerView.Adapter<NewsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(View, parent, false)
        return NewsViewHolder(view,View)

    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val current = items[position]
        holder.dateText.text = current.publishedAt.substring(0..9)
        holder.titleview.text = current.title
        holder.authorTextView.text = current.author
        updateButton(current.saved, holder.bookbut)
        if(current.urlToImage == "null")
            holder.imageview.setImageResource(R.drawable.image_not_loaded)
        else
            Glide.with(holder.itemView.context)
                .load(current.urlToImage)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(50)))
                .into(holder.imageview)

        holder.imageview.setOnClickListener{
            listener.onNewsClick(current)
        }
        holder.shareButton.setOnClickListener{
            listener.onShareClick(current.url)
        }
        holder.bookbut.setOnClickListener{
            current.saved = !current.saved
            holder.bookbut.animate().apply {
                duration=200
                scaleXBy(0.3f)
                scaleYBy(0.3f)
            }.withEndAction {
                updateButton(current.saved, holder.bookbut)
                listener.onSaveClick(current)
                holder.bookbut.animate().apply {
                    duration=300
                    scaleX(1f)
                    scaleY(1f)
                }
            }
        }
    }
    override fun getItemCount(): Int {
        return items.size
    }
    private fun updateButton(saved:Boolean, bookbut:ImageButton){
        val background = if(saved) R.drawable.bookmark_saved else R.drawable.bookmark_not_saved
        bookbut.setBackgroundResource(background)
    }
}

class NewsViewHolder(itemView: View,view: Int) : RecyclerView.ViewHolder(itemView) {
    val titleview: TextView = itemView.findViewById(if(view == R.layout.items_news) R.id.title else R.id.title_main)
    val authorTextView: TextView = itemView.findViewById(if(view == R.layout.items_news) R.id.authorview else R.id.authorview_main)
    val imageview: ImageView = itemView.findViewById(if(view == R.layout.items_news) R.id.newsImage else R.id.newsImage_main)
    val bookbut: ImageButton = itemView.findViewById(if(view == R.layout.items_news) R.id.bookmarkButton else R.id.bookmarkButton_main)
    val dateText: TextView = itemView.findViewById(if(view == R.layout.items_news) R.id.newsDateText else R.id.newsDateText_main)
    val shareButton: ImageButton = itemView.findViewById(if(view == R.layout.items_news) R.id.shareButton else R.id.shareButton_main)
}
interface newsClickListener{
    fun onNewsClick(news: DataNews)
    fun onShareClick(url:String)
    fun onSaveClick(news: DataNews)
}