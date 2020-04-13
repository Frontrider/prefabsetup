plugins {
    java
    application
    kotlin("jvm") version "1.3.70"
    id("com.jfrog.artifactory") version "4.13.0"
    `maven-publish`
    idea
}

group = "io.github.cottonmc.prefabmod"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven { setUrl("http://server.bbkr.space:8081/artifactory/libs-release") }
    maven { setUrl("http://server.bbkr.space:8081/artifactory/libs-snapshot") }

    maven {
        setUrl("https://libraries.minecraft.net")
    }

}

application{
    mainClassName="io.github.cottonmc.prefabmod.ApplicationKt"
}
// Minimum jvmTarget of 1.8 needed since Kotlin 1.1
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget= "1.8"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("no.tornado:tornadofx:1.7.17")
    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation(group = "com.google.code.gson", name = "gson", version = "2.8.6")
    // https://mvnrepository.com/artifact/com.google.http-client/google-http-client
    implementation(group = "com.google.http-client", name = "google-http-client", version = "1.34.2")
    // https://mvnrepository.com/artifact/net.lingala.zip4j/zip4j
    implementation(group= "net.lingala.zip4j", name= "zip4j", version= "2.5.0")
    implementation("io.github.cottonmc:functionapi-content:1.4-SNAPSHOT")
    implementation("io.github.cottonmc:functionapi-api:1.4")
    //implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
}

apply(from="./publishing.gradle")

