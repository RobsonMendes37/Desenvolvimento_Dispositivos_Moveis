package com.example.crudapp2

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM user_table")
    fun getAllUsers(): Flow<List<User>>

    @Update
    suspend fun updateUser(user: User): Int

    @Delete
    suspend fun deleteUser(user: User): Int

}