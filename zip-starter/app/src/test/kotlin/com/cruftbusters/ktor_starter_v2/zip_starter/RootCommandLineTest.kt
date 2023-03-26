package com.cruftbusters.ktor_starter_v2.zip_starter

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import picocli.CommandLine

class RootCommandLineTest : FunSpec({
  test("runs") {
    ExecuteResult.capture { CommandLine(RootCommandLine()).execute() } shouldBe ExecuteResult(0, "", "")
  }
})