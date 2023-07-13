package com.example.learn1.dataBase

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.learn1.Dao.ButtonDao
import com.example.learn1.Dao.NewsDao
import com.example.learn1.DataButtons
import com.example.learn1.DataNews

@androidx.room.Database(
    entities = [DataButtons::class,DataNews::class],
    version = 1
)
abstract class MyDatabase: RoomDatabase() {
    abstract val buttonDao: ButtonDao
    abstract val newsDao: NewsDao

    companion object {
        @Volatile
        var instance :MyDatabase? = null
        var newsInstance : MyDatabase? = null

        fun getButtonDatabase(context: Context): MyDatabase? {
            var tempinstance = instance
            if(instance!=null)
                return tempinstance
            else{
                synchronized(this){
                    val roomdatabaseInstance = Room.databaseBuilder(context,MyDatabase::class.java,"DataButtons").allowMainThreadQueries().build()
                    instance = roomdatabaseInstance
                     return roomdatabaseInstance
                }
            }

        }
        fun getNewsDatabase(context: Context): MyDatabase? {
            var tempinstance = newsInstance
            if(newsInstance!=null)
                return tempinstance
            else{
                synchronized(this){
                    val roomdatabaseInstance = Room.databaseBuilder(context,MyDatabase::class.java,"DataNews").allowMainThreadQueries().build()
                    newsInstance = roomdatabaseInstance
                    return roomdatabaseInstance
                }
            }

        }
    }
}