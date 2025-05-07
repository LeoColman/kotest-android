package br.com.colman.kotest

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.engine.extensions.EmptyExtensionRegistry
import io.kotest.core.spec.Spec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.spec.Materializer
import io.kotest.engine.spec.SpecInstantiator
import io.kotest.engine.test.names.DefaultDisplayNameFormatter
import kotlinx.coroutines.runBlocking
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier

class KotestRunnerAndroid(
  private val kClass: Class<out Spec>
) : Runner() {
  private val formatter = DefaultDisplayNameFormatter()

  override fun run(notifier: RunNotifier) {
    runBlocking {
      val listener = JUnitTestEngineListener(notifier)
      TestEngineLauncher(listener).withClasses(kClass.kotlin).launch()
    }
  }

  override fun getDescription(): Description {
    val spec = runBlocking { SpecInstantiator(EmptyExtensionRegistry, ProjectConfigResolver()).createAndInitializeSpec( kClass.kotlin).getOrThrow() }
    spec.duplicateTestNameMode = DuplicateTestNameMode.Warn
    val desc = Description.createSuiteDescription(spec::class.java)
    Materializer().materialize(spec).forEach { rootTest ->
      desc.addChild(
        describeTestCase(
          rootTest,
          formatter.format(rootTest)
        )
      )
    }
    return desc
  }
}
