@file:OptIn(io.kotest.common.KotestInternal::class)

package br.com.colman.kotest.android.extensions.robolectric

import android.app.Application
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.extensions.ConstructorExtension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.scopes.RootScope
import io.kotest.core.test.TestCase
import io.kotest.core.test.isRootTest
import io.kotest.engine.test.TestResult
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
class RobolectricExtension : ConstructorExtension, TestCaseExtension {

  private val runnerMap = WeakHashMap<Spec, ContainedRobolectricRunner>()
  private val sdkRunnerMap = WeakHashMap<Spec, Map<String, ContainedRobolectricRunner>>()


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

    val sdk: IntArray? =
      robolectricTestAnnotations.firstOrNull { it.sdk.isNotEmpty() }?.sdk
    val minSdk: Int? =
      robolectricTestAnnotations.firstOrNull { it.minSdk != -1 }?.minSdk
    val maxSdk: Int? =
      robolectricTestAnnotations.firstOrNull { it.maxSdk != -1 }?.maxSdk
    val fontScale: Float? =
      robolectricTestAnnotations.firstOrNull { it.fontScale != -1f }?.fontScale
    val application: KClass<out Application>? =
      robolectricTestAnnotations
        .firstOrNull { it.application != KotestDefaultApplication::class }?.application
    val qualifiers: String? =
      robolectricTestAnnotations.firstOrNull { it.qualifiers.isNotEmpty() }?.qualifiers
    val shadows: Array<KClass<*>>? =
      robolectricTestAnnotations.firstOrNull { it.shadows.isNotEmpty() }?.shadows
    val instrumentedPackages: Array<String>? =
      robolectricTestAnnotations.firstOrNull { it.instrumentedPackages.isNotEmpty() }?.instrumentedPackages

    return Config.Builder()
      .also { builder ->
        if (sdk != null) {
          builder.setSdk(*sdk)
        }
        if (minSdk != null) {
          builder.setMinSdk(minSdk)
        }
        if (maxSdk != null) {
          builder.setMaxSdk(maxSdk)
        }
        if (fontScale != null) {
          builder.setFontScale(fontScale)
        }
        if (application != null) {
          builder.setApplication(application.java)
        }
        if (qualifiers != null) {
          builder.setQualifiers(qualifiers)
        }
        if (shadows != null) {
          builder.setShadows(*shadows.map { it.java }.toTypedArray())
        }
        if (instrumentedPackages != null) {
          builder.setInstrumentedPackages(*instrumentedPackages)
        }
      }.build()
  }

  override fun <T : Spec> instantiate(clazz: KClass<T>): Spec? {
    clazz.findAnnotation<RobolectricTest>() ?: return null

    val config = clazz.getConfig()
    val sdks = config.sdk

    if (sdks.size <= 1) {
      val runner = ContainedRobolectricRunner(config)
      val spec = runner.sdkEnvironment.bootstrappedClass<Spec>(clazz.java).newInstance()
      runnerMap[spec] = runner
      return spec
    }

    // Multi-SDK: create a runner and spec for each SDK
    data class SdkEntry(val sdk: Int, val runner: ContainedRobolectricRunner, val spec: Spec)

    val sdkEntries = sdks.map { sdk ->
      val singleSdkConfig = Config.Builder(config).setSdk(sdk).build()
      val runner = ContainedRobolectricRunner(singleSdkConfig)
      val spec = runner.sdkEnvironment.bootstrappedClass<Spec>(clazz.java).newInstance()
      SdkEntry(sdk, runner, spec)
    }

    // First SDK uses the original test names, additional SDKs get [SDK XX] prefix
    val primary = sdkEntries.first()
    runnerMap[primary.spec] = primary.runner

    val nameToRunner = mutableMapOf<String, ContainedRobolectricRunner>()
    for (entry in sdkEntries.drop(1)) {
      for (test in entry.spec.tests()) {
        val prefixedName = test.name.copy(name = "[SDK ${entry.sdk}] ${test.name.name}")
        nameToRunner[prefixedName.name] = entry.runner
        (primary.spec as RootScope).add(test.copy(name = prefixedName))
      }
    }

    sdkRunnerMap[primary.spec] = nameToRunner
    return primary.spec
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
    val containedRobolectricRunner = sdkRunnerMap[testCase.spec]?.get(testCase.name.name)
      ?: runnerMap[testCase.spec]!!

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

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ApplyExtension(RobolectricExtension::class)
annotation class RobolectricTest(
  val sdk: IntArray = [],
  val minSdk: Int = -1,
  val maxSdk: Int = -1,
  val fontScale: Float = -1f,
  val application: KClass<out Application> = KotestDefaultApplication::class,
  val qualifiers: String = "",
  val shadows: Array<KClass<*>> = [],
  val instrumentedPackages: Array<String> = [],
)
