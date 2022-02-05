package com.liflymark.normalschedule.logic.utils.coil.util

import android.graphics.Bitmap
import androidx.annotation.Px


/** Private utility methods for Coil. */
internal object Utils {


    /** Return the in memory size of a [Bitmap] with the given width, height, and [Bitmap.Config]. */
    fun calculateAllocationByteCount(@Px width: Int, @Px height: Int, config: Bitmap.Config?): Int {
        return width * height * config.bytesPerPixel
    }

}
