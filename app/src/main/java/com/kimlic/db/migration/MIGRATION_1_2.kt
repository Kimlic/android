package com.kimlic.db.migration

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

object MIGRATION_1_2 : Migration(1, 2) {

    // Live

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE UNIQUE INDEX `index_user_account_address` ON `user` (`account_address`)")
    }
}