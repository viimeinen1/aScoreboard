group = "io.github.viimeinen1.ascoreboard"
version = "1.1.0"
description = "a Scoreboard plugin and library"

plugins {
  id("java-library")
  id("com.gradleup.shadow") version "9.6.1"
  id("com.vanniktech.maven.publish") version "0.37.0"
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
  compileOnly("io.papermc.paper:paper-api:26.2.build.+")
  compileOnly("me.clip:placeholderapi:2.12.3")
  implementation("fr.mrmicky:fastboard:2.2.1")
  implementation("io.github.viimeinen1.amsg:aMsg:1.1.2")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.shadowJar {
  relocate("fr.mrmicky.fastboard", "io.github.viimeinen1.ascoreboard.fastboard")
}

mavenPublishing {
  publishToMavenCentral()

  signAllPublications()
}

mavenPublishing {
  coordinates(group.toString(), name.toString(), version.toString())

  pom {
    name.set("aScoreboard")
    description.set("Scoreboard plugin and library")
    inceptionYear.set("2026")
    url.set("https://github.com/viimeinen1/ascoreboard/")
    licenses {
      license {
        name.set("MIT")
        url.set("https://opensource.org/licenses/MIT")
      }
    }
    developers {
      developer {
        id.set("viimeinen1")
        name.set("viimeinen1")
        url.set("https://github.com/viimeinen1/")
      }
    }
    scm {
      url.set("https://github.com/viimeinen1/ascoreboard/")
    }
  }
}