package com.cruftbusters.ktor_starter_v2.zip_starter

import java.io.ByteArrayOutputStream
import java.io.PrintStream

data class ExecuteResult(
  val exitCode: Int,
  val out: String,
  val err: String,
) {
  companion object {
    fun capture(block: () -> Int): ExecuteResult {
      val out = System.out
      val err = System.err
      val outCapture = ByteArrayOutputStream()
      val errCapture = ByteArrayOutputStream()
      System.setOut(PrintStream(outCapture))
      System.setErr(PrintStream(errCapture))
      return try {
        val result = block()
        ExecuteResult(result, outCapture.toString(), errCapture.toString())
      } finally {
        System.setOut(out)
        System.setErr(err)
      }
    }
  }
}