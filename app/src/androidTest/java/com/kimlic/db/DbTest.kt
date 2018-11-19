package com.kimlic.db

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import org.junit.After
import org.junit.Before

abstract class DbTest {

    private lateinit var _db: KimlicDB

    val db: KimlicDB
        get() = _db

    @Before
    fun initDb() {
        _db = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getContext(),
                KimlicDB::class.java
        ).build()
    }

    @After
    fun closeDb() {
        _db.close()
    }
}
