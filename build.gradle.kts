import java.nio.file.Files.isDirectory

plugins {
    id("com.github.johnrengelman.shadow")
    java
    idea
    id("fabric-loom")
    `maven-publish`
    kotlin("jvm")
    kotlin("kapt")
}

val modVersion: String by project
val mavenGroup: String by project
val archivesBaseName: String by project

version = modVersion
group = mavenGroup

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceSets["main"].java {
        srcDir("${buildDir.absolutePath}/generated/source/kaptKotlin/")
    }
}

base {
    archivesBaseName = archivesBaseName
}

minecraft {
//    accessTransformer.set(file('src/main/resources/META-INF/accesstransformer.cfg'))
}

configurations.api {
    extendsFrom(configurations["shadow"])
}


repositories {
    maven("http://maven.fabricmc.net/")
    maven("https://server.bbkr.space/artifactory/libs-release")
    maven("https://aperlambda.github.io/maven")
    maven("https://jitpack.io")
    maven("https://www.cursemaven.com")
}

dependencies {
    val minecraftVersion: String by project
    val yarnMappings: String by project
    val loaderVersion: String by project
    val fabricVersion: String by project
    val fabricKotlinVersion: String by project
    val reiVersion: String by project
//    val libGui: String by project
//    val spruceUi: String by project

    //to change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$yarnMappings:v2")
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")

    // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
    modImplementation("net.fabricmc:fabric-language-kotlin:$fabricKotlinVersion")

    modImplementation("curse.maven:roughly-enough-items:$reiVersion")

    // GUI Library
    // https://github.com/CottonMC/LibGui/wiki/Getting-Started-with-GUIs
//    modImplementation("io.github.cottonmc:LibGui:$libGui")
    // https://github.com/LambdAurora/SpruceUI
//    modImplementation("com.github.lambdaurora:spruceui:$spruceUi")
//    include("com.github.lambdaurora:spruceui:$spruceUi}")
//
//    shadow("org.aperlambda:lambdajcommon:1.8.1") {
//        exclude("com.google.code.gson")
//        exclude("com.google.guava")
//    }

    val rcVersion = "RebornCore:RebornCore-1.16:+"
    modApi (rcVersion) {
        exclude("net.fabricmc.fabric-api")
    }
    include(rcVersion)

    compileOnly(project(":annotations"))
    kapt(project(":processor"))

    // https://github.com/TechReborn/Energy
    // https://github.com/natanfudge/Working-Scheduler
    // https://github.com/Siphalor/nbt-crafting
}

val processResources = tasks.getByName<ProcessResources>("processResources") {
    inputs.property("version", project.version)
//
//    from(sourceSets.main.resources.srcDirs) {
//        include("META-INF/mods.toml")
//        expand("version": project.version)
//    }

//    from(sourceSets.main.resources.srcDirs) {
//        exclude("META-INF/mods.toml")
//    }

    filesMatching("fabric.mod.json") {
        filter { line -> line.replace("%VERSION%", "${project.version}") }
    }
}



val javaCompile = tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

val jar = tasks.getByName<Jar>("jar") {
    from("LICENSE")
}

val shadowJar = tasks.withType<Jar> {
    archiveClassifier.set("dev")

    from(sourceSets.main.get().output)

    val shadowFiles = configurations.shadow.get().files
        .map { if(it.isDirectory) it else zipTree(it) }
    from(shadowFiles)
}

val shadowRemapJar = tasks.withType<net.fabricmc.loom.task.RemapJarTask> {
    dependsOn(shadowJar)

    input.set(file("${project.buildDir}/libs/$archivesBaseName-$archiveVersion-dev.jar"))
    archiveFileName.set("${archivesBaseName}-${archiveVersion}.jar")
    addNestedDependencies.set(true)
}


// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
// if it is present.
// If you remove this task, sources will not be generated.
val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}