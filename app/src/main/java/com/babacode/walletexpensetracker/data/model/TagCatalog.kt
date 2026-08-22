package com.babacode.walletexpensetracker.data.model

// Closed set of tags, matching the reference design
// (refrence/src/lib/finance/types.ts: EXPENSE_TAGS / INCOME_TAGS).
// Tags are plain strings rather than an enum so an expense and an income
// transaction can each offer their own (overlapping) tag list, e.g. "Other"
// appears in both without needing two enum constants.
object TagCatalog {
    val EXPENSE_TAGS: List<String> = listOf(
        "Rent", "Food", "Utils", "Travel", "Shopping", "Health", "Entertainment", "Other"
    )

    val INCOME_TAGS: List<String> = listOf(
        "Salary", "Freelance", "Interest", "Gift", "Other"
    )

    val ALL_TAGS: List<String> = (EXPENSE_TAGS + INCOME_TAGS).distinct()

    fun tagsFor(transactionType: TransactionType): List<String> = when (transactionType) {
        TransactionType.EXPENSE -> EXPENSE_TAGS
        TransactionType.INCOME -> INCOME_TAGS
    }
}
