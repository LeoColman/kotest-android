package br.com.colman.kotest.android.matchers

import android.app.Activity
import android.os.Bundle
import br.com.colman.kotest.assertions.test.R

class TestActivity : Activity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.test_activity)
  }
}
