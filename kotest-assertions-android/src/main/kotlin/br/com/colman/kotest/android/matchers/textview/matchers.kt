package br.com.colman.kotest.android.matchers.textview

import android.content.res.Resources.Theme
import android.os.Build.VERSION_CODES
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun TextView.shouldHaveText(text: String) = this should haveText(text)
infix fun TextView.shouldNotHaveText(text: String) = this shouldNot haveText(text)

fun haveText(text: String) = object : Matcher<TextView> {
  override fun test(value: TextView) = MatcherResult(
    text.contentEquals(value.text),
    "TextView should have text $text but was ${value.text}",
    "TextView should not have text $text, but had"
  )

}

fun haveTextColorId(@ColorRes colorRes: Int, theme: Theme? = null) = object: Matcher<TextView> {
  override fun test(value: TextView): MatcherResult {
    val currentColor = value.currentTextColor
    val resourceColor = ResourcesCompat.getColor(value.resources, colorRes, theme)
    val resourceName = value.resources.getResourceName(colorRes)

    return MatcherResult(
      resourceColor == currentColor,
      "TextView should have color resource $resourceName(#$resourceColor) but was #$currentColor",
      "TextView should not have color $resourceName(#$resourceColor), but had"
    )
  }
}

infix fun TextView.shouldHaveTextColorId(@ColorRes colorId: Int) = shouldHaveTextColorId(colorId, null)
fun TextView.shouldHaveTextColorId(@ColorRes colorId: Int, theme: Theme? = null) =
  this should haveTextColorId(colorId, theme)

infix fun TextView.shouldNotHaveTextColorId(@ColorRes colorId: Int) = shouldNotHaveTextColorId(colorId, null)
fun TextView.shouldNotHaveTextColorId(@ColorRes colorId: Int, theme: Theme? = null) =
  this shouldNot haveTextColorId(colorId, theme)

infix fun TextView.shouldHaveTextColor(@ColorInt color: Int) = this should haveTextColor(color)
infix fun TextView.shouldNotHaveTextColor(@ColorInt color: Int) = this shouldNot haveTextColor(color)

fun haveTextColor(@ColorInt color: Int) = object: Matcher<TextView> {
  override fun test(value: TextView): MatcherResult {
    val currentColor = value.currentTextColor

    return MatcherResult(
      color == currentColor,
      "TextView should have color $color but was $currentColor",
      "TextView should not have color $color, but had"
    )
  }
}

@RequiresApi(VERSION_CODES.P)
fun TextView.shouldBeAllCaps() = this should beAllCaps()

@RequiresApi(VERSION_CODES.P)
fun TextView.shouldNotBeAllCaps() = this shouldNot beAllCaps()

@RequiresApi(VERSION_CODES.P)
fun beAllCaps() = object : Matcher<TextView> {
  override fun test(value: TextView) = MatcherResult(
    value.isAllCaps,
    "TextView should have AllCaps as transformation method",
    "TextView should not have AllCaps as transformation method"
  )
}
