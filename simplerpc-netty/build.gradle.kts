


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


val jacksonVersion = "2.9.8"
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.netty:netty-all:4.1.36.Final")
    compileOnly(project(":com.springmarker.simplerpc"))
}