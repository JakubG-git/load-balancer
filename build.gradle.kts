plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.9.22"
}

group = "pl.edu.agh.kis.dp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.hibernate.orm:hibernate-core:6.4.1.Final")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}