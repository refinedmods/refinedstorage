pluginManagement {
    repositories {
        jcenter()
        maven(url = "https://maven.fabricmc.net/")
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
project(":annotations").projectDir = file("../fabric_annotations/annotations")
project(":processor").projectDir = file("../fabric_annotations/processor")