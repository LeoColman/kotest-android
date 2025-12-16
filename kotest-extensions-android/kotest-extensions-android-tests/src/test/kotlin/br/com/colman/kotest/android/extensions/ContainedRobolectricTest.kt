package br.com.colman.kotest.android.extensions

import android.app.Application
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import br.com.colman.kotest.TestApplication
import br.com.colman.kotest.android.extensions.robolectric.RobolectricTest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.robolectric.Shadows.shadowOf

private class MockApplication : Application()

@RobolectricTest
class ContainedRobolectricRunnerDefaultApplicationTest : StringSpec({
  "Get the Application defined in AndroidManifest.xml" {
    ApplicationProvider.getApplicationContext<Application>()::class shouldBe TestApplication::class
  }
})

@RobolectricTest(application = MockApplication::class)
class ContainedRobolectricRunnerChangeApplicationTest : StringSpec({
  "Get the Application defined in RobolectricTest annotation" {
    ApplicationProvider.getApplicationContext<Application>()::class shouldBe MockApplication::class
  }
})

@RobolectricTest(sdk = Build.VERSION_CODES.O)
class ContainedRobolectricRunnerChangeApiLevelOTest : StringSpec({
  "Get the Build.VERSION_CODES.O" {
    Build.VERSION.SDK_INT shouldBe Build.VERSION_CODES.O
  }
})

@RobolectricTest(sdk = Build.VERSION_CODES.KITKAT)
class ContainedRobolectricRunnerChangeApiLevelKITKATTest : StringSpec({
  "Get the Build.VERSION_CODES.KITKAT" {
    Build.VERSION.SDK_INT shouldBe Build.VERSION_CODES.KITKAT
  }
})

@RobolectricTest(application = MockApplication::class)
abstract class ContainedRobolectricRunnerMergeTest : StringSpec()

@RobolectricTest(sdk = Build.VERSION_CODES.O)
class ContainedRobolectricRunnerMergeApiVersionTest : ContainedRobolectricRunnerMergeTest() {
  init {
    "Get the Build.VERSION_CODES.O" {
      Build.VERSION.SDK_INT shouldBe Build.VERSION_CODES.O
    }

    "Get the Application defined in parent RobolectricTest annotation" {
      ApplicationProvider.getApplicationContext<Application>()::class shouldBe MockApplication::class
    }
  }
}

@RobolectricTest(application = MockApplication::class, sdk = Build.VERSION_CODES.O)
abstract class ContainedRobolectricRunnerMergeOverwriteTest : StringSpec({
  "Get not Build.VERSION_CODES.O" {
    Build.VERSION.SDK_INT shouldNotBe Build.VERSION_CODES.O
  }
})

@Suppress("ClassName")
@RobolectricTest(sdk = Build.VERSION_CODES.O_MR1)
class ContainedRobolectricRunnerMergeOverwriteO_MR1Test : ContainedRobolectricRunnerMergeOverwriteTest()

@OptIn(ExperimentalCoroutinesApi::class)
@RobolectricTest
class ContainedRobolectricRunnerBehaviorSpecTest : BehaviorSpec({
  Context("Get the Application defined in AndroidManifest.xml") {
    Given("A application context") {
      val applicationContext = ApplicationProvider.getApplicationContext<Application>()

      When("Get class from application context") {
        val applicationClass = applicationContext::class

        Then("It should be TestApplication") {
          applicationClass shouldBe TestApplication::class
        }
      }
    }
  }

  Context("Collect the flow in TestScope") {
    Given("Some numbers and a SharedFlow") {
      val numbers = listOf(1, 2, 3)
      val sharedFlow = MutableSharedFlow<Int>()

      And("Launch to collect the flow") {
        Then("Collected the same numbers") {
          runTest {
            val collectedNumbers = mutableListOf<Int>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
              sharedFlow.collect { collectedNumbers.add(it) }
            }

            numbers.forEach { launch { sharedFlow.emit(it) } }

            testScheduler.advanceUntilIdle()

            collectedNumbers shouldBeEqual numbers
          }
        }
      }
    }
  }

  Context("Unit testing Android Handler using Robolectric") {
    Given("An Android Handler") {
      val handler = Handler(Looper.getMainLooper())

      When("Add a number on the Handler") {
        val numbers = mutableListOf<Long>()
        val currentTime = System.currentTimeMillis()
        handler.post { numbers.add(currentTime) }

        shadowOf(Looper.getMainLooper()).idle()

        Then("The Number added the list") {
          numbers.shouldNotBeEmpty().shouldContain(currentTime)
        }
      }
    }
  }
})
