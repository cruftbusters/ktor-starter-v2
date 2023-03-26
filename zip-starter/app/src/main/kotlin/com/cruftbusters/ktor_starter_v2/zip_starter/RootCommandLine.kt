package com.cruftbusters.ktor_starter_v2.zip_starter

import picocli.CommandLine
import kotlin.system.exitProcess

@CommandLine.Command
class RootCommandLine : Runnable {
  override fun run() {}
}

fun main(args: Array<String>): Unit = exitProcess(CommandLine(RootCommandLine()).execute(*args))
