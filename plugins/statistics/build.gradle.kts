plugins {
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.16"
    id("org.jetbrains.kotlin.jvm") version "2.1.20"
    id("com.gradleup.shadow") version "9.0.0-beta13"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

dependencies {
    paperweight.paperDevBundle("1.21.5-R0.1-SNAPSHOT")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}

tasks.assemble {
    dependsOn(tasks.reobfJar)
}

tasks {
    runServer {
        minecraftVersion("1.21.5")
    }
}