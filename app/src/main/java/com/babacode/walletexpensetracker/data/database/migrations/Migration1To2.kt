package com.babacode.walletexpensetracker.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// v1 -> v2: introduces the reference design's closed tag/payment-mode sets
// (data/model/TagCatalog.kt, data/model/PaymentMode.kt) and adds budget_table /
// recurring_table. `transaction_table`'s columns are unchanged in shape (tag and
// paymentType were already TEXT columns storing an enum's `.name`), so existing
// rows are remapped in place rather than rebuilding the table.
//
// Tag remap (old TransactionTag enum name -> new closed tag string), matched
// against transactionType where the old tag could plausibly be either kind:
//   FOOD/SHOPPING/TRAVELLING/ENTERTAINMENT/HEALTH/RENT/UTILS (expense)   -> direct equivalent
//   GIFT (income) -> "Gift", GIFT (expense) -> "Other" (no expense equivalent)
//   SALARY (income) -> "Salary", SALARY (expense) -> "Other"
//   EDUCATION/BILLS/INVESTMENT/COUPONS/CASHBACK/OTHER -> "Other" (no equivalent)
//
// Payment-mode remap (old PaymentType enum name -> new PaymentMode enum name):
//   CASH -> CASH, ONLINE -> ONLINE_BANKING
//   CARD -> CREDIT_CARD (old "Card" had no credit/debit distinction; confirmed with product)
//   GIFT ("Gift Coupons") -> ONLINE_BANKING (no equivalent; confirmed with product)
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            UPDATE transaction_table SET tag = CASE
                WHEN tag = 'FOOD' AND transactionType = 'EXPENSE' THEN 'Food'
                WHEN tag = 'SHOPPING' AND transactionType = 'EXPENSE' THEN 'Shopping'
                WHEN tag = 'TRAVELLING' AND transactionType = 'EXPENSE' THEN 'Travel'
                WHEN tag = 'ENTERTAINMENT' AND transactionType = 'EXPENSE' THEN 'Entertainment'
                WHEN tag = 'HEALTH' AND transactionType = 'EXPENSE' THEN 'Health'
                WHEN tag = 'RENT' AND transactionType = 'EXPENSE' THEN 'Rent'
                WHEN tag = 'UTILS' AND transactionType = 'EXPENSE' THEN 'Utils'
                WHEN tag = 'GIFT' AND transactionType = 'INCOME' THEN 'Gift'
                WHEN tag = 'SALARY' AND transactionType = 'INCOME' THEN 'Salary'
                ELSE 'Other'
            END
            """.trimIndent()
        )

        db.execSQL(
            """
            UPDATE transaction_table SET paymentType = CASE
                WHEN paymentType = 'CASH' THEN 'CASH'
                WHEN paymentType = 'ONLINE' THEN 'ONLINE_BANKING'
                WHEN paymentType = 'CARD' THEN 'CREDIT_CARD'
                WHEN paymentType = 'GIFT' THEN 'ONLINE_BANKING'
                ELSE 'CASH'
            END
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `budget_table` (
                `tag` TEXT NOT NULL,
                `limitAmount` REAL NOT NULL,
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `recurring_table` (
                `type` TEXT NOT NULL,
                `amount` REAL NOT NULL,
                `note` TEXT NOT NULL,
                `tag` TEXT NOT NULL,
                `mode` TEXT NOT NULL,
                `frequency` TEXT NOT NULL,
                `nextDate` INTEGER NOT NULL,
                `active` INTEGER NOT NULL,
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL
            )
            """.trimIndent()
        )
    }
}
