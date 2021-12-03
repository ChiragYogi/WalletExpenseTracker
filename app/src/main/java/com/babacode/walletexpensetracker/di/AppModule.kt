package com.babacode.walletexpensetracker.di

import android.content.Context
import androidx.room.Room
import com.babacode.walletexpensetracker.data.dao.TransactionDao
import com.babacode.walletexpensetracker.data.database.TransactionDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object AppModule {


    @Singleton
    @Provides
    fun provideTransactionDatabase(@ApplicationContext context: Context): TransactionDatabase {
        return Room.databaseBuilder(
            context,
            TransactionDatabase::class.java,
            "transaction_database"
        ).build()
    }


    @Singleton
    @Provides
    fun provideTransactionDao(transactionDatabase: TransactionDatabase) : TransactionDao {
        return transactionDatabase.getTransactionDao()
    }
}