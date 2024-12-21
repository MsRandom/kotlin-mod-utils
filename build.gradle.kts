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
        loaderVersion.set("36.2.34")
        minecraftVersion.set("1.16.5")

        dependsOn(registryAccessMixin)
    }

    forge("forge:1.18.2") {
        loaderVersion.set("40.2.0")
        minecraftVersion.set("1.18.2")
    }

    forge("forge:1.20.1") {
        loaderVersion.set("47.1.3")
        minecraftVersion.set("1.20.1")
    }

    neoforge("neoforge:1.21.1") {
        loaderVersion.set("21.1.90")
        minecraftVersion.set("1.21.1")
    }

    fabric("fabric:1.16.5") {
        loaderVersion.set("0.16.9")
        minecraftVersion.set("1.16.5")

        noClient()

        dependencies {
            fabricApi("0.42.0+1.16")
        }

        dependsOn(registryAccessMixin)
    }

    fabric("fabric:1.18.2") {
        loaderVersion.set("0.16.9")
        minecraftVersion.set("1.18.2")

        noClient()

        dependencies {
            fabricApi("0.77.0+1.18.2")
        }

        dependsOn(registryAccessMixin)
    }

    fabric("fabric:1.20.1") {
        loaderVersion.set("0.16.9")
        minecraftVersion.set("1.20.1")

        noClient()

        dependencies {
            fabricApi("0.92.2+1.20.1")
        }
    }

    fabric("fabric:1.21.1") {
        loaderVersion.set("0.16.9")
        minecraftVersion.set("1.21.1")

        noClient()

        dependencies {
            fabricApi("0.110.0+1.21.1")
        }
    }
}
