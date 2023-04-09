plugins {
  kotlin("jvm")
  id("org.jetbrains.dokka") version "1.8.10"
  `maven-publish`
  signing
}

dependencies {
  api("junit:junit:4.13.2")
  api("io.kotest:kotest-framework-engine:5.5.5")
  api("io.kotest:kotest-framework-discovery:5.5.5")
}

val sourcesJar by tasks.registering(Jar::class) {
  archiveClassifier.set("sources")
  from(sourceSets.getByName("main").allSource)
}

val javadocJar by tasks.registering(Jar::class) {
  dependsOn("dokkaHtml")
  archiveClassifier.set("javadoc")
  from("$buildDir/dokka")
}

publishing {
  repositories {
    maven("https://oss.sonatype.org/service/local/staging/deploy/maven2") {
      credentials {
        username = System.getenv("OSSRH_USERNAME")
        password = System.getenv("OSSRH_PASSWORD")
      }
    }
  }

  publications {
    register("mavenJava", MavenPublication::class) {
      from(components["java"])
      artifact(sourcesJar.get())
      artifact(javadocJar.get())

      groupId = "br.com.colman"
      artifactId = "kotest-runner-android"
      version = System.getenv("RELEASE_VERSION") ?: "local"

      pom {
        name.set("kotest-runner-android")
        description.set("Kotest Runner Android")
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
}

val signingKey: String? by project
val signingPassword: String? by project

signing {
  useGpgCmd()
  if (signingKey != null && signingPassword != null) {
    useInMemoryPgpKeys(signingKey, signingPassword)
  }

  sign(publishing.publications["mavenJava"])
}
