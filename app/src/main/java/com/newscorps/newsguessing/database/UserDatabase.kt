package com.newscorps.newsguessing.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.newscorps.newsguessing.entity.User

@Database(entities = [User::class],version = 1)
abstract class UserDatabase() :RoomDatabase(){

    abstract fun getUserDao(): UserDao

    companion object{

        var Instance : UserDatabase? = null

        fun getInstance(application: Application): UserDatabase?{

            if(Instance ==null){

                Instance = Room.databaseBuilder(application,
                    UserDatabase::class.java,"USER_DB")
                    .build()

            }

            return Instance
        }

    }


}