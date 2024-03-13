package com.codercampy.browser

import android.content.res.Resources
import kotlin.math.roundToInt

fun dpToPx(value: Int): Int {
    return (Resources.getSystem().displayMetrics.density * value).roundToInt()
}