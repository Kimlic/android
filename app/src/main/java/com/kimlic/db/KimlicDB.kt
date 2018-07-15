package com.kimlic.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.util.Log
import com.kimlic.KimlicApp

@Database(entities = arrayOf(User::class), version = 1)
abstract class KimlicDB : RoomDatabase() {

    init {
        Log.d("TAGDATABASE", "Room initializing...")
    }

    abstract fun userDao(): UserDao

    companion object {
        private var INSTANCE: KimlicDB? = null

        fun getInstance(context: Context? = KimlicApp.applicationContext()): KimlicDB? {
            if (INSTANCE == null) {
                synchronized(KimlicDB::class) {
                    INSTANCE = Room.databaseBuilder(KimlicApp.applicationContext(), KimlicDB::class.java, "kimlic.db").allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }

        fun destroyInstanse() {
            INSTANCE = null
        }
    }


}