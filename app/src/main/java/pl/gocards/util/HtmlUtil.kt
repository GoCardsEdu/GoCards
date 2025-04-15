/**
 * @author Grzegorz Ziemski
 */
package pl.gocards.util

import android.text.Html
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.core.text.HtmlCompat


fun String.newlineToHtmlBr(): String = replace("\n", "<br/>")

fun String.fromHtmlToAnnotatedString() =
    AnnotatedString.fromHtml(this.newlineToHtmlBr())

fun String.fromHtmlToSpanned() =
    Html.fromHtml(this.newlineToHtmlBr(), Html.FROM_HTML_MODE_COMPACT)

fun String.fromHtmlToHtmlCompat() =
    HtmlCompat.fromHtml(this.newlineToHtmlBr(), HtmlCompat.FROM_HTML_MODE_COMPACT)