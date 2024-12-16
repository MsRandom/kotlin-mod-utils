@file:OptIn(InternalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.InternalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.20"
    id("earth.terrarium.cloche") version "0.5.0"
}


repositories {
    mavenLocal()

    mavenCentral()

    maven("https://maven.msrandom.net/repository/root")
}

cloche {
    metadata {
        modId.set("kotlinutils")
    }

    val registryAccessMixin = cloche.common("registryAccessMixin") {
        accessWideners.from(files("src/registryAccessMixin/kotlinutils-registry-access.accessWidener"))
        mixins.from(files("src/registryAccessMixin/kotlinutils-registry-access.mixins.json"))
    }

    forge("forge:1.16.5") {
        loaderVersion.set("36.2.39")
        minecraftVersion.set("1.16.5")

        dependsOn(registryAccessMixin)
    }

    fabric("fabric:1.16.5") {
        loaderVersion.set("0.14.8")
        minecraftVersion.set("1.16.5")

        dependsOn(registryAccessMixin)
    }
}
