package br.com.colman.kotest.android.extensions.robolectric

import java.lang.reflect.Field
import java.lang.reflect.Type

internal fun Class<*>.getField(fieldClass: Type): Field {
  return declaredFields.firstOrNull { it.type == fieldClass }
    ?: throw IllegalStateException("Not found $fieldClass field.")
}
