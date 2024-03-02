package br.com.colman.kotest.android.extensions

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import br.com.colman.kotest.TestApplication
import br.com.colman.kotest.android.extensions.robolectric.RobolectricTest
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

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
