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
    minSdk = 19
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

  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
  }
}


dependencies {
  implementation("androidx.test:core-ktx:1.5.0")
  testImplementation(project(":kotest-extensions-android"))
  testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
  testImplementation("org.robolectric:robolectric:4.12.2")
  testImplementation("androidx.test.espresso:espresso-core:3.6.1")
  testImplementation("org.junit.vintage:junit-vintage-engine:5.11.3")
  androidTestImplementation("androidx.test:runner:1.5.2")
  androidTestImplementation("androidx.test:core:1.5.0")
  androidTestImplementation("androidx.test:rules:1.5.0")
}

tasks.withType<Test> {
  useJUnitPlatform()
}
