package com.babacode.walletexpensetracker.di

import android.content.Context
import androidx.room.Room
import com.babacode.walletexpensetracker.data.dao.BudgetDao
import com.babacode.walletexpensetracker.data.dao.RecurringDao
import com.babacode.walletexpensetracker.data.dao.TransactionDao
import com.babacode.walletexpensetracker.data.database.TransactionDatabase
import com.babacode.walletexpensetracker.data.database.migrations.MIGRATION_1_2
import com.babacode.walletexpensetracker.repository.BudgetRepository
import com.babacode.walletexpensetracker.repository.RecurringRepository
import com.babacode.walletexpensetracker.repository.TransactionRepository
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
        ).addMigrations(MIGRATION_1_2).build()
    }


    @Singleton
    @Provides
    fun provideTransactionDao(transactionDatabase: TransactionDatabase) : TransactionDao {
        return transactionDatabase.getTransactionDao()
    }

    @Singleton
    @Provides
    fun provideBudgetDao(transactionDatabase: TransactionDatabase): BudgetDao {
        return transactionDatabase.getBudgetDao()
    }

    @Singleton
    @Provides
    fun provideRecurringDao(transactionDatabase: TransactionDatabase): RecurringDao {
        return transactionDatabase.getRecurringDao()
    }


    @Singleton
    @Provides
    fun provideTransactionRepository(transactionDao: TransactionDao): TransactionRepository{
        return TransactionRepository(transactionDao)
    }

    @Singleton
    @Provides
    fun provideBudgetRepository(budgetDao: BudgetDao): BudgetRepository {
        return BudgetRepository(budgetDao)
    }

    @Singleton
    @Provides
    fun provideRecurringRepository(recurringDao: RecurringDao): RecurringRepository {
        return RecurringRepository(recurringDao)
    }


}