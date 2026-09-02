package com.babacode.walletexpensetracker.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.navigation3.scene.Scene

private const val NavTransitionDurationMs = 300

// Shared between the outer NavDisplay and the tab section's inner NavDisplay so both
// levels animate identically.
fun <T : Any> navForwardTransitionSpec(): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
    slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(NavTransitionDurationMs)
    ) togetherWith slideOutHorizontally(
        targetOffsetX = { -it },
        animationSpec = tween(NavTransitionDurationMs)
    )
}

fun <T : Any> navBackTransitionSpec(): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
    slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = tween(NavTransitionDurationMs)
    ) togetherWith slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(NavTransitionDurationMs)
    )
}

// predictivePopTransitionSpec's function type carries an extra swipe-edge Int parameter
// that popTransitionSpec's doesn't, so it needs its own (unused-parameter) variant.
fun <T : Any> navPredictiveBackTransitionSpec(): AnimatedContentTransitionScope<Scene<T>>.(Int) -> ContentTransform = {
    slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = tween(NavTransitionDurationMs)
    ) togetherWith slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(NavTransitionDurationMs)
    )
}

// Leaving transitionSpec unset does NOT mean "no animation" — NavDisplay falls back to its
// own default, a 700ms crossfade (see DEFAULT_TRANSITION_DURATION_MILLISECOND in
// NavDisplay.android.kt), which is slower than the 300ms slide above. This is the explicit
// zero-animation spec for cases (e.g. lateral bottom-nav tab switches) that want an instant cut.
fun <T : Any> navInstantTransitionSpec(): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
    EnterTransition.None togetherWith ExitTransition.None
}

fun <T : Any> navInstantPredictiveBackTransitionSpec(): AnimatedContentTransitionScope<Scene<T>>.(Int) -> ContentTransform = {
    EnterTransition.None togetherWith ExitTransition.None
}
