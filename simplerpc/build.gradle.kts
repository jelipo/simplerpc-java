import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.31"
}
buildscript {

    val kotlinVersion = "1.3.31"
    repositories {
        maven("https://repo.huaweicloud.com/repository/maven/")
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
        classpath("com.bmuschko:gradle-docker-plugin:4.8.1")
    }
}
apply(plugin = "kotlin")
apply(plugin = "kotlin-spring")
apply(plugin = "eclipse")
apply(plugin = "java")

group = "com.mayabot"
version = "0.0.1"

repositories {
    maven("https://repo.huaweicloud.com/repository/maven/")
    mavenCentral()
    mavenLocal()
    gradlePluginPortal()
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }
}

val jacksonVersion = "2.9.8"
dependencies {
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testCompile("org.jetbrains.kotlin:kotlin-test-junit5:1.3.31")
    implementation("cglib:cglib:3.2.12")
    compileOnly("io.netty:netty-all:4.1.36.Final")
    compileOnly(project(":simplerpc-netty"))
}