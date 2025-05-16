plugins {
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.16"
    id("org.jetbrains.kotlin.jvm") version "2.1.20"
    id("com.gradleup.shadow") version "9.0.0-beta13"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

repositories {
    mavenCentral()
    maven("https://repo.inventivetalent.org/repository/public/")
}

dependencies {
    paperweight.paperDevBundle("1.21.5-R0.1-SNAPSHOT")

    implementation("org.mineskin:java-client:3.0.3")
    implementation("org.jsoup:jsoup:1.20.1")
    implementation("org.java-websocket:Java-WebSocket:1.6.0")
}

tasks.assemble {
    dependsOn(tasks.reobfJar)
}

tasks {
    runServer {
        minecraftVersion("1.21.5")
    }
}