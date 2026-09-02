package com.babacode.walletexpensetracker.data.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.babacode.walletexpensetracker.data.database.migrations.MIGRATION_1_2
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Verifies MIGRATION_1_2 (data/database/migrations/Migration1To2.kt) doesn't crash and
// remaps every possible pre-existing value into the new closed tag/payment-mode sets,
// seeded against every old TransactionTag value x every old PaymentType value x both
// transaction types (the full cross-product a real populated v1 database could contain).
@RunWith(AndroidJUnit4::class)
class Migration1To2Test {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        TransactionDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    private val oldTags = listOf(
        "OTHER", "FOOD", "SHOPPING", "TRAVELLING", "ENTERTAINMENT", "HEALTH",
        "EDUCATION", "RENT", "BILLS", "GIFT", "INVESTMENT", "UTILS", "SALARY",
        "COUPONS", "CASHBACK"
    )
    private val oldPaymentTypes = listOf("CASH", "CARD", "ONLINE", "GIFT")
    private val oldTransactionTypes = listOf("EXPENSE", "INCOME")

    private val validExpenseTags =
        setOf("Rent", "Food", "Utils", "Travel", "Shopping", "Health", "Entertainment", "Other")
    private val validIncomeTags = setOf("Salary", "Freelance", "Interest", "Gift", "Other")
    private val validPaymentModes = setOf("CASH", "ONLINE_BANKING", "UPI", "CREDIT_CARD", "DEBIT_CARD")

    @Test
    fun migrate1To2_remapsEveryOldTagAndPaymentTypeIntoTheNewClosedSets() {
        val dbName = "migration-1-2-test"
        var db = helper.createDatabase(dbName, 1)

        var id = 1
        for (transactionType in oldTransactionTypes) {
            for (tag in oldTags) {
                for (paymentType in oldPaymentTypes) {
                    db.execSQL(
                        "INSERT INTO transaction_table " +
                            "(note, date, transactionType, amount, tag, paymentType, id) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)",
                        arrayOf<Any>("note-$id", id.toLong(), transactionType, 10.0, tag, paymentType, id)
                    )
                    id++
                }
            }
        }
        db.close()

        db = helper.runMigrationsAndValidate(dbName, 2, true, MIGRATION_1_2)

        val cursor = db.query("SELECT transactionType, tag, paymentType FROM transaction_table")
        var rowCount = 0
        cursor.use {
            while (it.moveToNext()) {
                rowCount++
                val transactionType = it.getString(0)
                val tag = it.getString(1)
                val paymentType = it.getString(2)

                assertTrue("unexpected paymentType '$paymentType'", paymentType in validPaymentModes)
                val validTags = if (transactionType == "EXPENSE") validExpenseTags else validIncomeTags
                assertTrue("unexpected tag '$tag' for $transactionType", tag in validTags)
            }
        }
        assertTrue(rowCount == oldTransactionTypes.size * oldTags.size * oldPaymentTypes.size)

        val budgetTableCount = db.query("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='budget_table'")
        budgetTableCount.use { it.moveToFirst(); assertTrue(it.getInt(0) == 1) }

        val recurringTableCount =
            db.query("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='recurring_table'")
        recurringTableCount.use { it.moveToFirst(); assertTrue(it.getInt(0) == 1) }

        db.close()
    }
}
