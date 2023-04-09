# Kotest Android

Unofficial integration between the [Kotest Framework](https://kotest.io) and
the [Android Platform](https://developer.android.com/docs). This repository aims to contain the tools necessary to run
Kotest in the complicated Android environment, with [`kotest-runner-android`](#kotest-runner-android) - a customized
version
of [`kotest-runner-junit4`](https://github.com/kotest/kotest/tree/master/kotest-runner/kotest-runner-junit4) - [`kotest-assertions-android`](#kotest-assertions-android)
containing matchers and assertions specific to Android

----

## Kotest Runner Android

A custom version
of [`kotest-runner-junit4`](https://github.com/kotest/kotest/tree/master/kotest-runner/kotest-runner-junit4) made
specifically to run Android Integration Tests.

### Adding the Runner to an Android Project

The SubProject [kotest-runner-android-tests](kotest-runner-android/kotest-runner-android-tests) should provide a good
sample if you need one.

#### Android Block Configuration

In the Android Block of your `build.gradle.kts` you need to ensure that the `testInstrumentationRunner` is set
to `"androidx.test.runner.AndroidJUnitRunner"`. Yes, the default runner is what we will leverage to run our tests.

```kotlin
android {
    defaultConfig {
        // ...
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}
```

#### Dependencies Configuration



To `androidTestImplementation` we are going to add `kotest-runner-android`:
```kotlin
  androidTestImplementation("br.com.colman:kotest-runner-android:VERSION")
```


## Kotest Assertions Android

Documentation WIP