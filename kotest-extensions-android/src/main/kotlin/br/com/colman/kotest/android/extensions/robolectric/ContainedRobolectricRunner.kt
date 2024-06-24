package br.com.colman.kotest.android.extensions.robolectric

import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.model.FrameworkMethod
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.internal.bytecode.InstrumentationConfiguration
import org.robolectric.pluginapi.config.Configurer
import org.robolectric.plugins.HierarchicalConfigurationStrategy
import org.robolectric.util.inject.Injector
import java.lang.reflect.Method

@RunWith(Enclosed::class)
internal class ContainedRobolectricRunner(
  config: Config
) : RobolectricTestRunner(PlaceholderTest::class.java, kotestInjector(config)) {

  private val placeHolderMethod: FrameworkMethod = children[0]
  val sdkEnvironment = getSandbox(placeHolderMethod).also {
    configureSandbox(it, placeHolderMethod)
  }
  private val bootStrapMethod = sdkEnvironment.bootstrappedClass<Any>(testClass.javaClass)
    .getMethod(PlaceholderTest::bootStrapMethod.name)

  fun containedBefore() {
    Thread.currentThread().contextClassLoader = sdkEnvironment.robolectricClassLoader
    super.beforeTest(sdkEnvironment, placeHolderMethod, bootStrapMethod)
  }

  fun containedAfter() {
    super.afterTest(placeHolderMethod, bootStrapMethod)
    super.finallyAfterTest(placeHolderMethod)
    Thread.currentThread().contextClassLoader = ContainedRobolectricRunner::class.java.classLoader
  }

  override fun createClassLoaderConfig(method: FrameworkMethod?): InstrumentationConfiguration {
    return InstrumentationConfiguration.Builder(super.createClassLoaderConfig(method))
      .doNotAcquirePackage("io.kotest")
      .build()
  }

  class PlaceholderTest {
    @org.junit.Test
    fun testPlaceholder() {
    }

    fun bootStrapMethod() {
    }
  }

  class KotestHierarchicalConfigurationStrategy(
    private val config: Config,
    configurers: Array<Configurer<*>>
  ) : HierarchicalConfigurationStrategy(*configurers) {
    override fun getConfig(testClass: Class<*>?, method: Method?): ConfigurationImpl {
      val configurationImpl = super.getConfig(testClass, method)
      val config = (configurationImpl.get(Config::class.java) as Config)
      val newConfig = Config.Builder(config).overlay(this.config).build()
      configurationImpl.map()[Config::class.java] = newConfig
      return configurationImpl
    }
  }

  companion object {
    private fun kotestInjector(config: Config): Injector {
      val defaultInjector = defaultInjector().bind(Config::class.java, config).build()
      return Injector.Builder(defaultInjector, ContainedRobolectricRunner::class.java.classLoader)
        .build()
    }
  }
}
