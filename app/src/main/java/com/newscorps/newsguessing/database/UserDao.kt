package com.newscorps.newsguessing.database

import androidx.room.*
import com.newscorps.newsguessing.entity.User

@Dao
interface UserDao{

    @Query("Select * from User")
    suspend fun getUser():List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: User)

    @Query("Select * from User where name = :userName ")
    suspend fun getUserByName(userName: String): User

    @Update
    suspend fun updateUser(currentUser: User)

}