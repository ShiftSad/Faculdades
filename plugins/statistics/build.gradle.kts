plugins {
  id("io.papermc.paperweight.userdev") version "2.0.0-beta.16"
  id("org.jetbrains.kotlin.jvm") version "2.1.20"
}

dependencies {
  paperweight.paperDevBundle("1.21.5-R0.1-SNAPSHOT")
}

tasks.assemble {
    dependsOn(tasks.reobfJar)
}