# Kotest Android

[![License](https://img.shields.io/github/license/LeoColman/kotest-android)](LICENSE)
![Maintenance](https://img.shields.io/maintenance/yes/2023)
[![Kotest Repository](https://img.shields.io/badge/Kotest-kotest.io-green)](https://kotest.io/)

Unofficial integration between the [Kotest Framework](https://kotest.io) and
the [Android Platform](https://developer.android.com/docs). This repository aims to contain the tools necessary to run
Kotest in the complicated Android environment, with [`kotest-runner-android`](#kotest-runner-android) - a customized
version
of [`kotest-runner-junit4`](https://github.com/kotest/kotest/tree/master/kotest-runner/kotest-runner-junit4) - [`kotest-assertions-android`](#kotest-assertions-android)
containing matchers and assertions specific to Android

----

## Kotest Runner Android

![Maven Central](https://img.shields.io/maven-central/v/br.com.colman/kotest-runner-android)

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

## Kotest Extensions Android
![Maven Central](https://img.shields.io/maven-central/v/br.com.colman/kotest-extensions-android)

#### Dependencies Configuration
To `testImplementation` we are going to add `kotest-extensions-android`:

```kotlin
  testImplementation("br.com.colman:kotest-extensions-android:VERSION")
```

#### Available Extensions

##### Robolectric Extension

The Robolectric Extension enables tests to operate under the [Robolectric](https://robolectric.org/) environment. To
utilize this feature, adorn the unit tests that you wish to run with Robolectric with the `@RobolectricTest` annotation.
Tests will be executed within the integrated Robolectric environment. `@Config` annotations can be used for Robolectric
specific configurations.

Here is a Kotlin example of how to use the annotation:

```kotlin
@RobolectricTest(sdk = Build.VERSION_CODES.O)
class ContainedRobolectricRunnerMergeApiVersionTest {
  init {
    "Get the Build.VERSION_CODES.O" {
      Build.VERSION.SDK_INT shouldBe Build.VERSION_CODES.O
    }
  }
}
```



## Kotest Assertions Android

![Maven Central](https://img.shields.io/maven-central/v/br.com.colman/kotest-assertions-android)

#### Dependencies Configuration

To `androidTestImplementation` we are going to add `kotest-assertions-android`:

```kotlin
  androidTestImplementation("br.com.colman:kotest-assertions-android:VERSION")
```

#### Available Matchers

Documentation and format to be improved.


<details>
<summary>Open Spoiler for Details</summary>

| View                                      |                                                          |
|-------------------------------------------|----------------------------------------------------------|
| `view.shouldBeVisible()`                  | Asserts that the view visibility is VISIBLE              |
| `view.shouldBeInvisible()`                | Asserts that the view visibility is INVISIBLE            |
| `view.shouldBeGone()`                     | Asserts that the view visibility is GONE                 |
| `view.shouldHaveContentDescription()`     | Asserts that the view has any content description        |
| `view.shouldHaveContentDescription(desc)` | Asserts that the view has `desc` as Content Description  |
| `view.shouldHaveTag(key, value)`          | Asserts that the view has a tag `key` with value `value` |
| `view.shouldHaveTag(any)`                 | Asserts that the view's tag is `any`                     |
| `view.shouldBeEnabled()`                  | Asserts that the view is enabled                         |
| `view.shouldBeFocused()`                  | Asserts that the view has focus                          |
| `view.shouldBeFocusable()`                | Asserts that the view is focusable                       |
| `view.shouldBeFocusableInTouchMode()`     | Asserts that the view is focusable in touch mode         |
| `view.shouldBeClickable()`                | Asserts that the view is clickable                       |
| `view.shouldBeLongClickable()`            | Asserts that the view is long clickable                  |

| TextView                                |                                                                  |
|-----------------------------------------|------------------------------------------------------------------|
| `tv.shouldHaveText(text)`               | Asserts that the text view has text `text`                       |
| `tv.shouldHaveTextColorId(id)`          | Asserts that the text color is the same from color resource `id` |
| `tv.shouldHaveTextColor(colorInt)`      | Asserts that the text color is `colorInt`                        |
| `tv.shouldBeAllCaps()`                  | Asserts that the textview is marked with the `isAllCaps` flag    |
| `tv.shouldHaveTextAlignment(alignment)` | Asserts that the text alignment is `alignment`                   |

</details>