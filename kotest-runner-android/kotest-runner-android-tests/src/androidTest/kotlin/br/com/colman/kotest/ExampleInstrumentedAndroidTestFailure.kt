package br.com.colman.kotest

import androidx.test.platform.app.InstrumentationRegistry
import io.kotest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KotestRunnerAndroid::class)
class ExampleInstrumentedAndroidTestFailure : FunSpec({

    test("Use app context") {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        appContext.packageName shouldBe "io.kotest.fff"
    }

})
