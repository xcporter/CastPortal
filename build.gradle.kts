plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.10"
    id("org.openjfx.javafxplugin") version "0.0.9"
    id("com.xcporter.jpkg") version "0.0.7"
    id("com.xcporter.metaview") version "0.0.4"
}

group = "com.xcporter"
version = "1.0.4"

repositories {
    mavenCentral()
}

dependencies {
    implementation("no.tornado:tornadofx:1.7.20")
    implementation("de.jensd:fontawesomefx-fontawesome:4.7.0-9.1.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.3.5")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.12.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.3")
    implementation("org.jsoup:jsoup:1.13.1")
    implementation("joda-time:joda-time:2.10.6")
    implementation("ws.schild:jave-all-deps:2.5.0")
    implementation("io.ktor:ktor-client-apache:1.4.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "11"
}

tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = "11"
}

javafx {
    version = "14"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.media", "javafx.graphics")
}

jpkg {
    packageName = "CastPortal"
    vendor = "Alexander Porter"
    copyright = "2021"
    description = "Minimalist Podcatcher"
    resourceDir = "src/main/resources/icons/"
    menuGroup = "Podcast Farm"
    mainClass = "MainKt"

    verbose = true
    mac {
        icon = "src/main/resources/icons/CastPortal.icns"
        name = "Cast Portal"
        signingIdentity = env("IDENTITY")
        bundleName = "com.xcporter.castportal"
        userName = env("USERNAME")
        password = env("PASSWORD")
    }
    windows {
        icon = "src/main/resources/icons/Logo_Red.ico"
    }
    linux {
        icon = "src/main/resources/icons/Logo_Red.png"
        packageDependencies = mutableListOf("openjfx")
    }
}

generateUml {
    classTree {}
    functionTree {}
}
