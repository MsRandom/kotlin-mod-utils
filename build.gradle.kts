import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.mpp.DefaultKotlinDependencyHandler

plugins {
    kotlin("multiplatform") version "1.5.+"
    id("minecraft-codev-remapper") version "1.0"
    id("minecraft-codev-access-widener") version "1.0"
    id("minecraft-codev-forge") version "1.0"
    id("minecraft-codev-fabric") version "1.0"
}

group = "net.msrandom"

repositories {
    mavenCentral()
    minecraft()
    maven(url = "https://libraries.minecraft.net/")

    maven(url = "https://maven.fabricmc.net/")

    maven(url = "https://maven.minecraftforge.net/") {
        metadataSources {
            gradleMetadata()
            mavenPom()
            artifact()
        }
    }
}

fun KotlinDependencyHandler.mappings(dependency: Any): Dependency? {
    this as DefaultKotlinDependencyHandler
    return dependencies.add((parent as KotlinSourceSet).name + "Mappings", dependency)
}

fun KotlinDependencyHandler.patches(dependency: Any): Dependency? {
    this as DefaultKotlinDependencyHandler
    return dependencies.add((parent as KotlinSourceSet).name + "Patches", dependency)
}

fun KotlinDependencyHandler.accessWideners(dependency: Any): Dependency? {
    this as DefaultKotlinDependencyHandler
    return dependencies.add((parent as KotlinSourceSet).name + "AccessWideners", dependency)
}

val minecraftVersionAttribute = Attribute.of("net.msrandom.unifiedbuilds.minecraftVersion", String::class.java)
val modLoaderAttribute = Attribute.of("net.msrandom.unifiedbuilds.modLoader", String::class.java)
val commonMappings: Configuration by configurations.creating

kotlin {
    val commonMain by sourceSets.getting {
        dependencies {
            implementation(dependencies.create(group = "com.mojang", name = "datafixerupper", version = "4.1.27"))

            // Use 1.12.2 as the base common dependency because it's the lowest version, so the dependency selector wouldn't use it to override other versions
            implementation(minecraft(MinecraftType.Common, "1.12.2").remapped(mappingsConfiguration = commonMappings.name))
        }
    }

    jvm("forge12") {
        val minecraftVersion = "1.12.2"

        attributes.attribute(minecraftVersionAttribute, minecraftVersion)
        attributes.attribute(modLoaderAttribute, "forge")

        val main by compilations.getting {
            defaultSourceSet {
                dependencies {
                    patches(mappings(dependencies.create(group = "net.minecraftforge", name = "forge", version = "$minecraftVersion-14.23.5.2860", classifier = "userdev3"))!!)
                    mappings(files("src/$name/mappings.tiny"))
                    accessWideners(files("src/$name/kotlinutils.accessWidener"))

                    implementation(
                        minecraft(MinecraftType.Common, minecraftVersion)
                            .patched(name + "Patches")
                            .remapped(mappingsConfiguration = name + "Mappings")
                            .accessWidened(name + "AccessWideners")
                    )
                }
            }
        }

        val client by compilations.creating {
            defaultSourceSet {
                dependencies {
                    implementation(minecraft(MinecraftType.Client, minecraftVersion).patched(main.defaultSourceSetName + "Patches").remapped(mappingsConfiguration = main.defaultSourceSetName + "Mappings"))
                }

                dependsOn(main.defaultSourceSet)
            }
        }

        withJava()
    }

/*    jvm("forge14")
    jvm("fabric14")*/
    jvm("forge16") {
        attributes.attribute(minecraftVersionAttribute, "1.16.5")
        attributes.attribute(modLoaderAttribute, "forge")
    }

    jvm("fabric16") {
        attributes.attribute(minecraftVersionAttribute, "1.16.5")
        attributes.attribute(modLoaderAttribute, "fabric")
    }

    sourceSets {
/*        val forge14Main by getting {
            dependencies {
                patches(mappings(dependencies.create(group = "net.minecraftforge", name = "forge", version = "1.14.4-28.2.26", classifier = "userdev"))!!)
                mappings(minecraft(MinecraftType.ClientMappings, "1.14.4"))

                implementation(minecraft(MinecraftType.Common, "1.14.4").patched(name + "Patches").remapped(mappingsConfiguration = name + "Mappings"))
            }
        }

        val fabric14Main by getting {
            dependencies {
                mappings(minecraft(MinecraftType.ClientMappings, "1.14.4"))

                implementation(minecraft(MinecraftType.Common, "1.14.4").remapped(mappingsConfiguration = name + "Mappings"))
            }
        }*/

        val forge16Main by getting {
            dependencies {
                patches(mappings(dependencies.create(group = "net.minecraftforge", name = "forge", version = "1.16.5-36.2.39", classifier = "userdev"))!!)
                mappings(minecraft(MinecraftType.ClientMappings, "1.16.5"))

                implementation(minecraft(MinecraftType.Common, "1.16.5").patched(name + "Patches").remapped(mappingsConfiguration = name + "Mappings"))
            }
        }

        val fabric16Main by getting {
            dependencies {
                mappings(minecraft(MinecraftType.ClientMappings, "1.16.5"))

                implementation(minecraft(MinecraftType.Common, "1.16.5").remapped(mappingsConfiguration = name + "Mappings"))
                implementation(dependencies.create(group = "net.fabricmc", name = "fabric-loader", version = "0.14.8"))
            }
        }
    }
}

dependencies {
    commonMappings(files("src/forge12Main/mappings.tiny"))
}
