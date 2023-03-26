plugins {
  id("org.jetbrains.kotlin.jvm") version "1.8.10"
  kotlin("plugin.serialization") version "1.8.10"
  application
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("info.picocli:picocli:4.7.1")
  testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.5")
  testImplementation("io.kotest:kotest-runner-junit5-jvm:5.5.5")
  testImplementation("io.ktor:ktor-client-apache:2.2.4")
  testImplementation("io.ktor:ktor-client-content-negotiation:2.2.4")
  testImplementation("io.ktor:ktor-client-core:2.2.4")
}

application {
  mainClass.set("com.cruftbusters.ktor_starter_v2.zip_starter.RootCommandLineKt")
}

tasks.named<Test>("test") {
  useJUnitPlatform()
}
