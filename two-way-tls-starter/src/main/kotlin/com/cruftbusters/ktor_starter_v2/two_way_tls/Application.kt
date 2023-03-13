package com.cruftbusters.ktor_starter_v2.two_way_tls

import io.ktor.server.application.*

fun main(args: Array<String>): Unit =
  io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
}
