package com.babacode.walletexpensetracker.utiles

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import com.babacode.walletexpensetracker.R


//the inset will be only used for this view
fun View.applyEdgeToEdgeInsets(
        typeMask: Int = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.ime(),
        propagateInsets: Boolean = false,
        block: MarginLayoutParams.(InsetsAccumulator) -> Unit) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(typeMask)
        val initialTop = if (view.getTag(R.id.initial_margin_top) != null) {
            view.getTag(R.id.initial_margin_top) as Int
        } else {
            view.setTag(R.id.initial_margin_top, view.marginTop)
            view.marginTop
        }

        val initialBottom = if (view.getTag(R.id.initial_margin_bottom) != null) {
            view.getTag(R.id.initial_margin_bottom) as Int
        } else {
            view.setTag(R.id.initial_margin_bottom, view.marginBottom)
            view.marginBottom
        }

        val initialLeft = if (view.getTag(R.id.initial_margin_left) != null) {
            view.getTag(R.id.initial_margin_left) as Int
        } else {
            view.setTag(R.id.initial_margin_left, view.marginLeft)
            view.marginLeft
        }

        val initialRight = if (view.getTag(R.id.initial_margin_right) != null) {
            view.getTag(R.id.initial_margin_right) as Int
        } else {
            view.setTag(R.id.initial_margin_right, view.marginRight)
            view.marginRight
        }

        val insetsAccumulator = InsetsAccumulator(
                initialTop = initialTop,
                insetsTop = insets.top,
                initialBottom = initialBottom,
                insetsBottom = insets.bottom,
                initialLeft = initialLeft,
                insetsLeft = insets.left,
                initialRight = initialRight,
                insetsRight = insets.right,
        )

        view.updateLayoutParams<MarginLayoutParams> {
            apply { block(insetsAccumulator) }
        }
        if (propagateInsets) windowInsets else WindowInsetsCompat.CONSUMED
    }
}

fun View.applyEdgeToEdgeInsetsAtTopLeftRight() = applyEdgeToEdgeInsets {
    leftMargin = it.left
    topMargin = it.top
    rightMargin = it.right
}

fun View.applyEdgeToEdgeInsetsAtLeftRight() = applyEdgeToEdgeInsets {
    leftMargin = it.left
    rightMargin = it.right
}

fun View.applyEdgeToEdgeInsetsAtBottomLeftRight() = applyEdgeToEdgeInsets {
    leftMargin = it.left
    bottomMargin = it.bottom
    rightMargin = it.right
}

fun View.applyEdgeToEdgeInsetsToAllSide() = applyEdgeToEdgeInsets {
    leftMargin = it.left
    topMargin = it.top
    rightMargin = it.right
    bottomMargin = it.bottom
}

//The insets will be passed down to child view
fun View.applyEdgeToEdgeInsetsPadding(typeMask: Int = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.ime(),
                                      applyTopInset: Boolean = true,
                                      applyBottomInset: Boolean = true,
                                      applyLeftInset: Boolean = true,
                                      applyRightInset: Boolean = true) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val innerPadding = windowInsets.getInsets(typeMask)

        val left = if (applyLeftInset) innerPadding.left else view.paddingLeft
        val right = if (applyRightInset) innerPadding.right else view.paddingRight
        val top = if (applyTopInset) innerPadding.top else view.paddingTop
        val bottom = if (applyBottomInset) innerPadding.bottom else view.paddingBottom
        view.setPadding(left, top, right, bottom)
        windowInsets
    }
}

//This function is only used to set header image height based on height of statubar
fun View.applyHeaderHeightWithInsets(
        relatedViews: List<View> = emptyList(),
        baseHeightResId: Int,
        typeMask: Int = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.ime()
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(typeMask)
        val height = view.resources.getDimensionPixelOffset(baseHeightResId) + insets.top
        view.layoutParams.height = height
        view.requestLayout()

        relatedViews.forEach { relatedView ->
            relatedView.layoutParams.height = height
            relatedView.requestLayout()
        }

        WindowInsetsCompat.CONSUMED
    }
}


data class InsetsAccumulator(
        private val initialTop: Int,
        private val insetsTop: Int,
        private val initialBottom: Int,
        private val insetsBottom: Int,
        private val initialLeft: Int,
        private val insetsLeft: Int,
        private val initialRight: Int,
        private val insetsRight: Int,
) {
    val top: Int
        get() = initialTop + insetsTop
    val bottom: Int
        get() = initialBottom + insetsBottom

    val left: Int
        get() = initialLeft + insetsLeft
    val right: Int
        get() = initialRight + insetsRight
}
