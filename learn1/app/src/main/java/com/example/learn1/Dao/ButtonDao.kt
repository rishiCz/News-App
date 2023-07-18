package com.example.learn1.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.learn1.DataClass.DataButtons

@Dao
interface ButtonDao {
    @Insert
    fun insertButton(button: DataButtons)

    @Delete
    fun deleteButton(button: DataButtons)

    @Query("SELECT * FROM DataButtons")
    fun getButtonList(): LiveData<MutableList<DataButtons>>


}