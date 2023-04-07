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