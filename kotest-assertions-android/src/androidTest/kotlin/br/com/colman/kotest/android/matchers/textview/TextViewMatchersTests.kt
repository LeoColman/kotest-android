package br.com.colman.kotest.android.matchers.textview

import android.graphics.Color
import android.widget.TextView
import androidx.test.core.app.launchActivity
import br.com.colman.kotest.FreeSpec
import br.com.colman.kotest.android.matchers.TestActivity
import br.com.colman.kotest.assertions.test.R
import io.kotest.assertions.throwables.shouldThrow

class TextViewMatchersTests : FreeSpec({
  "Have Text" {
    val scenario = launchActivity<TestActivity>()

    scenario.onActivity {
      it.findViewById<TextView>(R.id.textView).also { tv ->
        tv shouldHaveText "Kotest!"
        tv shouldNotHaveText "a"
        tv shouldNotHaveText ""

        shouldThrow<AssertionError> { tv shouldHaveText "a" }
        shouldThrow<AssertionError> { tv shouldHaveText "" }
        shouldThrow<AssertionError> { tv shouldNotHaveText "Kotest!" }
      }
    }
  }

  "Have Color" {
    val scenario = launchActivity<TestActivity>()

    scenario.onActivity {
      val tv = it.findViewById<TextView>(R.id.textViewColored)

      tv shouldHaveTextColor Color.BLACK
      tv shouldNotHaveTextColor Color.RED
      tv shouldHaveTextColorId android.R.color.black
      tv shouldNotHaveTextColorId android.R.color.white


      shouldThrow<AssertionError> { tv shouldHaveTextColor Color.RED }
      shouldThrow<AssertionError> { tv shouldNotHaveTextColor Color.BLACK }
      shouldThrow<AssertionError> { tv shouldNotHaveTextColorId android.R.color.black }
      shouldThrow<AssertionError> { tv shouldHaveTextColorId android.R.color.white }
    }
  }

  "All Caps" {
    val scenario = launchActivity<TestActivity>()

    scenario.onActivity {
      val tv = it.findViewById<TextView>(R.id.textViewAllCaps)

      tv.shouldBeAllCaps()
      shouldThrow<AssertionError> { tv.shouldNotBeAllCaps() }

      tv.shouldNotBeAllCaps()
      shouldThrow<AssertionError> { tv.shouldBeAllCaps() }
    }
  }
})
