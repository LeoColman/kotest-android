package br.com.colman.kotest

import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import io.kotest.matchers.shouldBe
import org.junit.runner.RunWith

//@RunWith(KotestRunnerAndroid::class)
class ExampleInstrumentedAndroidTestPass : FunSpec({

    test("Use app context") {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        appContext.packageName shouldBe "br.com.colman.kotest"
    }

  context("Nested") {
    test("Use app context 1") {
      val appContext = InstrumentationRegistry.getInstrumentation().targetContext
      appContext.packageName shouldBe "br.com.colman.kotest"
    }
    test("Use app context 2") {
      val appContext = InstrumentationRegistry.getInstrumentation().targetContext
      appContext.packageName shouldBe "br.com.colman.kotest"
    }
  }

  context("Launching activity") {
    val activity = ActivityScenario.launch(TestActivity::class.java)

    activity.use {
      it.onActivity {
        it.findViewById<TextView>(R.id.textView).text shouldBe "Hello World!"
      }
    }
  }

})
