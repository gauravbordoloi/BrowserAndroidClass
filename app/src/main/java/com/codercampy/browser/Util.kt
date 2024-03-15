package com.codercampy.browser

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

fun dpToPx(value: Int): Int {
    return (Resources.getSystem().displayMetrics.density * value).roundToInt()
}

fun parseDate(date: Long): String {
    return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
}

fun shareArticle(context: Context, articleItem: HomeArticleItem) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, articleItem.link)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}