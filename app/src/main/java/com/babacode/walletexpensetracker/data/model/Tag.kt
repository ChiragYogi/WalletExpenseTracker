package com.babacode.walletexpensetracker.data.model

import android.graphics.drawable.Drawable
import android.media.Image

data class Tag (val image: Int, val tagTitle: String){
    override fun toString(): String {
        return  tagTitle
    }
}