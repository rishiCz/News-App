package com.example.learn1.dataBase

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.learn1.Dao.ButtonDao
import com.example.learn1.DataClass.DataButtons

@androidx.room.Database(
    entities = [DataButtons::class],
    version = 1
)
abstract class MyDatabase: RoomDatabase() {
    abstract val buttonDao: ButtonDao

    companion object {
        @Volatile
        var instance :MyDatabase? = null

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
    }
}