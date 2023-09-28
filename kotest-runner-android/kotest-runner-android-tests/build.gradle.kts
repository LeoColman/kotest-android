plugins {
  id("com.android.application")
  kotlin("android")
}

android {
  namespace = "br.com.colman.kotest"
  compileSdk = 33

  defaultConfig {
    applicationId = "br.com.colman.kotest"
    targetSdk = 33
    minSdk = 21
    versionCode = 100
    versionName = "1.0.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  packagingOptions {
    exclude("META-INF/**")
    exclude("win32-x86/**")
    exclude("win32-x86-64/**")
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}


dependencies {
  androidTestImplementation(project(":kotest-runner-android"))
  androidTestImplementation("androidx.test:runner:1.5.2")
  androidTestImplementation("androidx.test:core:1.5.0")
  androidTestImplementation("androidx.test:rules:1.5.0")

}
