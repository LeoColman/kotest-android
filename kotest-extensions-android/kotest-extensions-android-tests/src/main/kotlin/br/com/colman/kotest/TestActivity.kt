package br.com.colman.kotest

import android.app.Activity
import android.os.Bundle
import br.com.colman.kotest.R.layout.xml_view

class TestActivity : Activity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(xml_view)
  }
}
