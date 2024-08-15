package br.com.colman.kotest.android.extensions.robolectric

import android.app.Application
import io.kotest.core.extensions.ConstructorExtension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.AutoScan
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.isRootTest
import org.robolectric.annotation.Config
import java.util.WeakHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.time.Duration

/**
 * We override TestCaseExtension to configure the Robolectric environment because TestCase intercept
 * occurs on the same thread the test is run.  This is unfortunate because it is run for every test,
 * rather than every spec. But the SpecExtension intercept is run on a different thread.
 */
@AutoScan
class RobolectricExtension : ConstructorExtension, TestCaseExtension {

  private val runnerMap = WeakHashMap<Spec, ContainedRobolectricRunner>()


  private fun Class<*>.getParentClass(): List<Class<*>> {
    if (superclass == null) return listOf()
    return listOf(superclass) + superclass.getParentClass()
  }

  private fun KClass<*>.getConfig(): Config {
    val configAnnotations =
      listOf(this.java).plus(this.java.getParentClass())
        .mapNotNull { it.kotlin.findAnnotation<Config>() }
        .asSequence()

    val configAnnotation = configAnnotations.firstOrNull()

    if (configAnnotation != null) {
      return Config.Builder(configAnnotation).build()
    }

    val robolectricTestAnnotations =
      listOf(this.java).plus(this.java.getParentClass())
        .mapNotNull { it.kotlin.findAnnotation<RobolectricTest>() }
        .asSequence()

    val application: KClass<out Application>? =
      robolectricTestAnnotations
        .firstOrNull { it.application != KotestDefaultApplication::class }?.application
    val sdk: Int? = robolectricTestAnnotations.firstOrNull { it.sdk != -1 }?.takeUnless { it.sdk == -1 }?.sdk

    return Config.Builder()
      .also { builder ->
        if (application != null) {
          builder.setApplication(application.java)
        }

        if (sdk != null) {
          builder.setSdk(sdk)
        }
      }.build()
  }

  override fun <T : Spec> instantiate(clazz: KClass<T>): Spec? {
    clazz.findAnnotation<RobolectricTest>() ?: return null

    val runner = ContainedRobolectricRunner(clazz.getConfig())

    val spec = runner.sdkEnvironment.bootstrappedClass<Spec>(clazz.java).newInstance()
    runnerMap[spec] = runner
    return spec
  }

  override suspend fun intercept(
    testCase: TestCase,
    execute: suspend (TestCase) -> TestResult,
  ): TestResult {
    return try {
      runTest(testCase, execute)
    } catch (t: Throwable) {
      // Without this the whole test class will be silently be skipped
      // if something throws
      TestResult.Error(Duration.ZERO, t)
    }
  }

  private suspend fun runTest(
    testCase: TestCase,
    execute: suspend (TestCase) -> TestResult,
  ): TestResult {
    // FIXED: Updated code based on https://github.com/kotest/kotest/issues/2717
    val hasRobolectricAnnotation =
      testCase.spec::class.annotations.any { annotation ->
        annotation.annotationClass.qualifiedName == RobolectricTest::class.qualifiedName
      }

    return if (hasRobolectricAnnotation) {
      runTestRobolectric(testCase, execute)
    } else {
      execute(testCase)
    }
  }

  private suspend fun runTestRobolectric(
    testCase: TestCase,
    execute: suspend (TestCase) -> TestResult,
  ): TestResult {
    val containedRobolectricRunner = runnerMap[testCase.spec]!!
    if (testCase.isRootTest()) {
      containedRobolectricRunner.containedBefore()
    }
    val result = execute(testCase)
    if (testCase.isRootTest()) {
      containedRobolectricRunner.containedAfter()
    }
    return result
  }
}

internal class KotestDefaultApplication : Application()

annotation class RobolectricTest(
  val application: KClass<out Application> = KotestDefaultApplication::class,
  val sdk: Int = -1,
)
