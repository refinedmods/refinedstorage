pluginManagement {
    repositories {
        jcenter()
        maven("https://maven.fabricmc.net/")
        maven("https://jitpack.io")
        gradlePluginPortal()
    }

    val loomVersion: String by settings
    val kotlinVersion: String by settings
    plugins {
        id("com.github.johnrengelman.shadow") version("6.0.0")
        id("fabric-loom") version loomVersion
        kotlin("jvm") version kotlinVersion
        kotlin("kapt") version kotlinVersion
    }
}

include(":annotations", ":processor")
project(":annotations").projectDir = file("../FabricSeams/annotations")
project(":processor").projectDir = file("../FabricSeams/processor")