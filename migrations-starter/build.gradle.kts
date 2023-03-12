plugins {
  kotlin("jvm") version "1.8.10"
  id("io.ktor.plugin") version "2.2.4"
  id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
}

group = "com.cruftbusters.ktor_starter_v2.migrations_starter"
version = "0.0.1"
application {
  mainClass.set("io.ktor.server.netty.EngineMain")

  val isDevelopment: Boolean = project.ext.has("development")
  applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("ch.qos.logback:logback-classic:1.2.11")
  implementation("com.h2database:h2:2.1.214")
  implementation("io.ktor:ktor-server-config-yaml:2.2.4")
  implementation("io.ktor:ktor-server-core-jvm:2.2.4")
  implementation("io.ktor:ktor-server-netty-jvm:2.2.4")
  implementation("org.postgresql:postgresql:42.5.1")
  testImplementation("io.kotest:kotest-runner-junit5-jvm:5.5.5")
  testImplementation("io.ktor:ktor-server-tests-jvm:2.2.4")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.8.10")
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}