import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
  kotlin("android")
  kotlin("kapt")
  id("com.android.library")
}

android {
  compileSdk = 33
  defaultConfig {
    minSdk = 21
    targetSdk = 33

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  packagingOptions {
    exclude("META-INF/AL2.0")
    exclude("META-INF/LGPL2.1")
    exclude("META-INF/licenses/**")
    exclude("win32-x86/**")
    exclude("win32-x86-64/**")
  }
}

configurations {
  androidTestImplementation.get().exclude("org.jetbrains", "annotations")
}

dependencies {
  implementation("androidx.core:core-ktx:1.10.0")
  implementation("io.kotest:kotest-assertions-core:5.5.5")

  androidTestImplementation(project(":kotest-runner-android"))
  androidTestImplementation("androidx.test:runner:1.5.2")
  androidTestImplementation("androidx.test:core:1.5.0")
  androidTestImplementation("androidx.test:rules:1.5.0")
  androidTestImplementation("androidx.test:core-ktx:1.5.0")

}
