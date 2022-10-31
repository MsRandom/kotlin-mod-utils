rootProject.name = "kotlin-mod-utils"

pluginManagement {
    repositories {
        mavenLocal()
        // maven(url = "https://maven.msrandom.net/repository/root")
        maven(url = "https://maven.minecraftforge.net/")
        maven(url = "https://maven.quiltmc.org/repository/release/")
        maven(url = "https://maven.fabricmc.net/")
        gradlePluginPortal {
            content {
                excludeGroup("org.apache.logging.log4j")
            }
        }
    }
}
