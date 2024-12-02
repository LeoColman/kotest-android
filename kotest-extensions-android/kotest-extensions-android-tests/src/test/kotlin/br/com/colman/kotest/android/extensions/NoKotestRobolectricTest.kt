package br.com.colman.kotest.android.extensions

import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import br.com.colman.kotest.R
import br.com.colman.kotest.TestActivity
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NoKotestRobolectricTest {
  @Test
  fun `TextView in TestActivity has Hello World!`() {
    launch(TestActivity::class.java)

    onView(withId(R.id.textView))
      .check(matches(isDisplayed()))
      .check(matches(withText("Hello World!")))
  }
}
