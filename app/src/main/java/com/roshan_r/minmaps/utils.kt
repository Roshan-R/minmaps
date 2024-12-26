package com.roshan_r.minmaps

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable


object  Utils {
    var sIconSize: Int = -1

    fun convertIconToBitmap(
        context: Context,
        drawable: Drawable
    ): Bitmap { /*  ww  w  .j  a v a2  s.c  om*/

        if (sIconSize == -1) {
            sIconSize = context.resources.getDimensionPixelSize(
                android.R.dimen.app_icon_size
            )
        }

        return toBitmap(drawable, sIconSize, sIconSize)
    }

    private fun toBitmap(drawable: Drawable, width: Int, height: Int): Bitmap {
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        drawable.bounds = Rect(0, 0, width, height)
        drawable.draw(c)

        return bmp
    }

}