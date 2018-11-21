package com.kimlic.db

import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory
import android.arch.persistence.room.testing.MigrationTestHelper
import android.database.sqlite.SQLiteConstraintException
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.kimlic.db.migration.MIGRATION_1_2
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DataBaseMigrationTest {

    // Constants

    companion object {
        private const val TEST_DB = "migration_test"
        private const val QUERY = "INSERT INTO user(id, first_name, last_name, kim_quantity, mnemonic, portrait_file, portrait_preview_file, account_address, inserted_at) values (null, 'Volodymyr', 'Babenko', 5, 'embody castle gasp swing dry buyer tree tortoise thumb chronic bean quiz', 'portrait_file', 'portrait_preview_file','0x44977374d6347c893e4c93857e75271bd246c35d', '1542542895404')"
    }

    // Rule

    @Rule
    @JvmField
    val helper: MigrationTestHelper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), KimlicDB::class.java.canonicalName, FrameworkSQLiteOpenHelperFactory())

    @Test(expected = SQLiteConstraintException::class)
    @Throws(IOException::class)
    fun migrate1to2test() {

        helper.createDatabase(TEST_DB, 1).apply {
            // db has schema version 1. insert some data using SQL queries.
            // You cannot use DAO classes because they expect the latest schema.
            execSQL(QUERY)
            close()
        }

        helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2).apply {
            execSQL(QUERY)
            close()
        }
    }
}