plugins {
    alias(libs.plugins.loom)
    alias(libs.plugins.loom.legacy)
}

group = "dev.refactoring"
version = properties["version"]!!

repositories {
    mavenCentral()
    maven("https://raw.githubusercontent.com/BleachDev/cursed-mappings/main/")
}

val gameVersion by properties
val loaderVersion by properties

val mappings by properties

dependencies {
    minecraft("com.mojang:minecraft:$gameVersion")
    mappings("net.legacyfabric:yarn:1.8.9+build.$mappings:v2")

    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")

    implementation(libs.joml)
    implementation(libs.fastutil)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

loom {
    accessWidenerPath = file("src/main/resources/carbon.accesswidener")
}