package br.com.colman.kotest.android.extensions

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import br.com.colman.kotest.TestApplication
import br.com.colman.kotest.android.extensions.robolectric.RobolectricTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

/**
 * Regression tests for https://github.com/LeoColman/kotest-android/issues/35, where every test but
 * the first one failed whenever the isolation mode was not SingleInstance.
 *
 * Each spec asserts that Robolectric is alive in *every* test, and the `timesUsed` counter asserts
 * the isolation mode is really in effect: under SingleInstance it would keep incrementing, so a
 * regression that silently ignores the isolation mode fails these tests too.
 */
@RobolectricTest(sdk = [Build.VERSION_CODES.O])
class InstancePerLeafRobolectricTest : StringSpec() {
  override fun isolationMode() = IsolationMode.InstancePerLeaf

  private var timesUsed = 0

  init {
    "First test has a Robolectric environment" { useRobolectric() }
    "Second test has a Robolectric environment" { useRobolectric() }
    "Third test has a Robolectric environment" { useRobolectric() }
  }

  private fun useRobolectric() {
    timesUsed++
    timesUsed shouldBe 1 // a fresh spec per leaf, so this test is the first user of this instance
    ApplicationProvider.getApplicationContext<Application>()::class shouldBe TestApplication::class
    Build.VERSION.SDK_INT shouldBe Build.VERSION_CODES.O
  }
}

@RobolectricTest
class InstancePerLeafNestedRobolectricTest : BehaviorSpec() {
  override fun isolationMode() = IsolationMode.InstancePerLeaf

  private var timesUsed = 0

  init {
    Given("A Robolectric environment") {
      When("The first branch runs") {
        Then("The first leaf has an application context") { useRobolectric() }
        Then("A sibling leaf also has an application context") { useRobolectric() }
      }

      When("A second branch runs") {
        Then("Its leaf also has an application context") { useRobolectric() }
      }
    }
  }

  private fun useRobolectric() {
    timesUsed++
    timesUsed shouldBe 1
    ApplicationProvider.getApplicationContext<Application>()::class shouldBe TestApplication::class
  }
}

@RobolectricTest
class InstancePerRootRobolectricTest : BehaviorSpec() {
  override fun isolationMode() = IsolationMode.InstancePerRoot

  private var timesUsed = 0

  init {
    Given("The first root") {
      Then("Its first leaf has an application context") { useRobolectric(expectedTimesUsed = 1) }
      Then("Its second leaf shares the root's spec instance") { useRobolectric(expectedTimesUsed = 2) }
    }

    Given("The second root") {
      Then("It runs in a fresh spec instance") { useRobolectric(expectedTimesUsed = 1) }
    }
  }

  private fun useRobolectric(expectedTimesUsed: Int) {
    timesUsed++
    timesUsed shouldBe expectedTimesUsed // a fresh spec per root, shared by the leaves under it
    ApplicationProvider.getApplicationContext<Application>()::class shouldBe TestApplication::class
  }
}
