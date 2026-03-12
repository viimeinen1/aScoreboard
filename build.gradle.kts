group = "io.github.viimeinen1.ascoreboard"
version = "0.1.0"
description = "a scoreboard plugin"

plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.3.2"
}

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven { url = uri("https://repo.extendedclip.com/releases/") }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.12.2")
    implementation("fr.mrmicky:fastboard:2.1.5")
    implementation("io.github.viimeinen1.amsg:aMsg:1.1.2")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.shadowJar {
    relocate("fr.mrmicky.fastboard", "com.github.viimeinen1.ascoreboard.fastboard")
}