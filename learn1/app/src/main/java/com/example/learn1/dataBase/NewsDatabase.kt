package com.example.learn1.dataBase

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.learn1.Dao.NewsDao
import com.example.learn1.DataClass.DataNews

@androidx.room.Database(
    entities = [DataNews::class],
    version = 1
)
abstract class NewsDatabase: RoomDatabase() {
    abstract val newsDao: NewsDao

    companion object {
        @Volatile
        var newsInstance : NewsDatabase? = null
        fun getNewsDatabase(context: Context): NewsDatabase? {
            var tempinstance = newsInstance
            if(newsInstance!=null)
                return tempinstance
            else{
                synchronized(this){
                    val roomdatabaseInstance = Room.databaseBuilder(context,NewsDatabase::class.java,"DataNews").allowMainThreadQueries().build()
                    newsInstance = roomdatabaseInstance
                    return roomdatabaseInstance
                }
            }

        }
    }
}