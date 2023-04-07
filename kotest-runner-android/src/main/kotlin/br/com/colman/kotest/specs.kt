package br.com.colman.kotest

import org.junit.runner.RunWith

@RunWith(KotestRunnerAndroid::class)
abstract class FunSpec(body: io.kotest.core.spec.style.FunSpec.() -> Unit = {}) :
   io.kotest.core.spec.style.FunSpec(body)

@RunWith(KotestRunnerAndroid::class)
abstract class StringSpec(body: io.kotest.core.spec.style.StringSpec.() -> Unit = {}) :
   io.kotest.core.spec.style.StringSpec(body)

@RunWith(KotestRunnerAndroid::class)
abstract class ShouldSpec(body: io.kotest.core.spec.style.ShouldSpec.() -> Unit = {}) :
   io.kotest.core.spec.style.ShouldSpec(body)

@RunWith(KotestRunnerAndroid::class)
abstract class DescribeSpec(body: io.kotest.core.spec.style.DescribeSpec.() -> Unit = {}) :
   io.kotest.core.spec.style.DescribeSpec(body)

@RunWith(KotestRunnerAndroid::class)
abstract class ExpectSpec(body: io.kotest.core.spec.style.ExpectSpec.() -> Unit = {}) :
   io.kotest.core.spec.style.ExpectSpec(body)

@RunWith(KotestRunnerAndroid::class)
abstract class FeatureSpec(body: io.kotest.core.spec.style.FeatureSpec.() -> Unit = {}) :
   io.kotest.core.spec.style.FeatureSpec(body)

@RunWith(KotestRunnerAndroid::class)
abstract class FreeSpec(body: io.kotest.core.spec.style.FreeSpec.() -> Unit = {}) :
   io.kotest.core.spec.style.FreeSpec(body)

@RunWith(KotestRunnerAndroid::class)
abstract class WordSpec(body: io.kotest.core.spec.style.WordSpec.() -> Unit = {}) :
   io.kotest.core.spec.style.WordSpec(body)

@RunWith(KotestRunnerAndroid::class)
abstract class BehaviorSpec(body: io.kotest.core.spec.style.BehaviorSpec.() -> Unit = {}) :
   io.kotest.core.spec.style.BehaviorSpec(body)


