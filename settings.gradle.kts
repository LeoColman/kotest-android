pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
}
dependencyResolutionManagement {
  repositories {
    mavenCentral()
    google()
  }
}


rootProject.name = "kotest-android"

include(":kotest-runner-android", ":kotest-runner-android:kotest-runner-android-tests")

include(":kotest-assertions-android")
include(":kotest-extensions-android", ":kotest-extensions-android:kotest-extensions-android-tests")
