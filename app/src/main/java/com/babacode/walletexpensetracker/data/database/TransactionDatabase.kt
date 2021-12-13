package com.babacode.walletexpensetracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.babacode.walletexpensetracker.data.dao.TransactionDao
import com.babacode.walletexpensetracker.data.model.Transaction


@Database(entities = [Transaction::class], version = 1, exportSchema = true)

abstract class TransactionDatabase: RoomDatabase() {

    abstract fun getTransactionDao(): TransactionDao

}