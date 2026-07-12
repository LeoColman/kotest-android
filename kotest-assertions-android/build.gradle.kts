import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
  kotlin("android")
  kotlin("kapt")
  id("com.android.library")
  id("org.jetbrains.dokka") version "1.9.10"
  `maven-publish`
  signing
    id("com.gradleup.nmcp")
}


kotlin { jvmToolchain(11) }

group = "br.com.colman"
version = "1.1.5"

android {
  namespace = "br.com.colman.kotest.assertions"
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
  kotlinOptions { jvmTarget = "11"}

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
  // Exclude mordant FFM modules that require minSdk 26+ (JVM 21+ feature not needed on Android)
  all {
    exclude(group = "com.github.ajalt.mordant", module = "mordant-jvm-ffm-jvm")
    exclude(group = "com.github.ajalt.mordant", module = "mordant-jvm-ffm")
  }
}

dependencies {
  implementation("androidx.core:core-ktx:1.10.0")
  implementation("io.kotest:kotest-assertions-core:6.2.2")

  androidTestImplementation(project(":kotest-runner-android"))
  androidTestImplementation("androidx.test:runner:1.5.2")
  androidTestImplementation("androidx.test:core:1.5.0")
  androidTestImplementation("androidx.test:rules:1.5.0")
  androidTestImplementation("androidx.test:core-ktx:1.7.0")

}

val sourcesJar by tasks.registering(Jar::class) {
  archiveClassifier.set("sources")
  from(kotlin.sourceSets["main"].kotlin.srcDirs)
  from(android.sourceSets["main"].java.srcDirs)
}

val javadocJar by tasks.registering(Jar::class) {
  dependsOn("dokkaHtml")
  archiveClassifier.set("javadoc")
  from("$buildDir/dokka")
}

publishing {
  publications.create<MavenPublication>("mavenJava") {
    afterEvaluate { from(components["release"]) }
    artifact(javadocJar.get())
    artifact(sourcesJar.get())

    groupId = project.group.toString()
    artifactId = "kotest-assertions-android"
    version = project.version.toString()

    pom {
      name.set("kotest-assertions-android")
      description.set("Kotest Assertions Android")
      url.set("https://www.github.com/LeoColman/kotest-android")


      scm {
        connection.set("scm:git:http://www.github.com/LeoColman/kotest-android/")
        developerConnection.set("scm:git:http://github.com/LeoColman/")
        url.set("https://www.github.com/LeoColman/")
      }

      licenses {
        license {
          name.set("Apache License 2.0")
          url.set("https://opensource.org/licenses/Apache-2.0")
        }
      }

      developers {
        developer {
          id.set("LeoColman")
          name.set("Leonardo Colman Lopes")
          email.set("dev@leonardo.colman.com.br")
        }
      }
    }
  }
}

tasks.named("dokkaHtml").dependsOn("kaptReleaseKotlin")

val signingKey: String? by project
val signingPassword: String? by project

signing {
  useGpgCmd()
  if (signingKey != null && signingPassword != null) {
    useInMemoryPgpKeys(signingKey, signingPassword)
  }

  sign(publishing.publications)
}

nmcp {
  publishAllPublicationsToCentralPortal {
    username.set(System.getenv("SONATYPE_USERNAME"))
    password.set(System.getenv("SONATYPE_PASSWORD"))
    publishingType = "AUTOMATIC"
  }
}
