plugins {
  kotlin("jvm") version "2.2.0" apply false
  kotlin("android") version "2.2.0" apply false
  id("com.android.application") version "8.13.2" apply false
  id("com.gradleup.nmcp.aggregation").version("1.0.1")
}

nmcpAggregation {
  centralPortal {
    username.set(System.getenv("SONATYPE_USERNAME"))
    password.set(System.getenv("SONATYPE_PASSWORD"))
    publishingType = "AUTOMATIC"
  }
}

dependencies {
  // Add all dependencies here 
  nmcpAggregation(project(":kotest-runner-android"))
  nmcpAggregation(project(":kotest-assertions-android"))
  nmcpAggregation(project(":kotest-extensions-android"))
}

