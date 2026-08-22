package com.babacode.walletexpensetracker.ui.navigation

import kotlinx.serialization.Serializable

// Matches the reference design's insights-detail "kind" search param
// (refrence/src/routes/insights-detail.tsx): which slice of Insights was tapped.
@Serializable
enum class InsightKind {
    TAG,
    MODE,
    MONTH,
    TOP
}
