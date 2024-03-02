package br.com.colman.kotest

import android.app.Application

class TestApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    println("Created")
  }
}
