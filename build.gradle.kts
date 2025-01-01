plugins {
    kotlin("jvm") version "2.1.0"
    id("earth.terrarium.cloche") version "0.7.1"
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

    val common11651201 = common("common:1.16.5-1.20.1")

    val fabricCommon = common("fabric:common") {
        dependencies {
            fabricApi("0.42.0+1.16")
        }

        dependsOn(registryAccessMixin)
    }

    fabric("fabric:1.16.5") {
        loaderVersion.set("0.16.9")
        minecraftVersion.set("1.16.5")

        server()

        dependencies {
            fabricApi("0.42.0+1.16")
        }

        dependsOn(fabricCommon)
        dependsOn(common11651201)
    }

    fabric("fabric:1.18.2") {
        loaderVersion.set("0.16.9")
        minecraftVersion.set("1.18.2")

        server()

        dependencies {
            fabricApi("0.77.0+1.18.2")
        }

        dependsOn(fabricCommon)
        dependsOn(common11651201)
    }

    fabric("fabric:1.20.1") {
        loaderVersion.set("0.16.9")
        minecraftVersion.set("1.20.1")

        server()

        dependencies {
            fabricApi("0.92.2+1.20.1")
        }

        dependsOn(fabricCommon)
    }

    fabric("fabric:1.21.1") {
        loaderVersion.set("0.16.9")
        minecraftVersion.set("1.21.1")

        server()

        dependencies {
            fabricApi("0.110.0+1.21.1")
        }

        dependsOn(fabricCommon)
    }

    forge("forge:1.16.5") {
        loaderVersion.set("36.2.34")
        minecraftVersion.set("1.16.5")

        server()

        dependsOn(registryAccessMixin)
        dependsOn(common11651201)
    }

    forge("forge:1.18.2") {
        loaderVersion.set("40.2.0")
        minecraftVersion.set("1.18.2")

        server()

        dependsOn(common11651201)
    }

    forge("forge:1.20.1") {
        loaderVersion.set("47.1.3")
        minecraftVersion.set("1.20.1")

        server()
    }

    neoforge("neoforge:1.21.1") {
        loaderVersion.set("21.1.90")
        minecraftVersion.set("1.21.1")

        server()
    }
}
