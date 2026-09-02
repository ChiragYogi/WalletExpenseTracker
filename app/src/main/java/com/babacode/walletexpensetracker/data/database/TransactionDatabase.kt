package com.babacode.walletexpensetracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.babacode.walletexpensetracker.data.dao.BudgetDao
import com.babacode.walletexpensetracker.data.dao.RecurringDao
import com.babacode.walletexpensetracker.data.dao.TransactionDao
import com.babacode.walletexpensetracker.data.model.Budget
import com.babacode.walletexpensetracker.data.model.RecurringRule
import com.babacode.walletexpensetracker.data.model.Transaction


// exportSchema is off: androidx.room:room-compiler got pulled to 2.8.4 (transitively
// required by androidx.hilt:hilt-compiler, added for @HiltWorker support — see
// worker/RecurringPostWorker.kt) ships schema-bundle $$serializer classes that are ABI-
// incompatible with the kotlinx-serialization-core version its own POM resolves to
// (AbstractMethodError on GeneratedSerializer.typeParametersSerializers() the moment KSP
// reads any schema JSON back, including a freshly-exported one). schemas/.../1.json and
// 2.json on disk were hand-regenerated under 2.8.4 and are accurate for the current
// entities, but re-enabling this will need a Room release that fixes the mismatch (or a
// verified-working kotlinx-serialization pin) — check before flipping it back on.
@Database(
    entities = [Transaction::class, Budget::class, RecurringRule::class],
    version = 2,
    exportSchema = false
)
abstract class TransactionDatabase: RoomDatabase() {

    abstract fun getTransactionDao(): TransactionDao

    abstract fun getBudgetDao(): BudgetDao

    abstract fun getRecurringDao(): RecurringDao

}
