package com.babacode.walletexpensetracker.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Radius scale ported from the reference design's --radius: 1rem base
// (refrence/src/styles.css), notably rounder than Material3's defaults.
val RadiusSmall = 10.dp
val RadiusMedium = 13.dp
val RadiusLarge = 16.dp
val RadiusExtraLarge = 22.dp
val RadiusTwoExtraLarge = 28.dp
val RadiusThreeExtraLarge = 36.dp

val ShapeSmall = RoundedCornerShape(RadiusSmall)
val ShapeMedium = RoundedCornerShape(RadiusMedium)
val ShapeLarge = RoundedCornerShape(RadiusLarge)
val ShapeExtraLarge = RoundedCornerShape(RadiusExtraLarge)
val ShapeTwoExtraLarge = RoundedCornerShape(RadiusTwoExtraLarge)
val ShapeThreeExtraLarge = RoundedCornerShape(RadiusThreeExtraLarge)

val WalletShapes = Shapes(
    extraSmall = ShapeSmall,
    small = ShapeMedium,
    medium = ShapeLarge,
    large = ShapeExtraLarge,
    extraLarge = ShapeTwoExtraLarge,
)
