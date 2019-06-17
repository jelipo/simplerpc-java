plugins {
    java
    application
}

group = "com.mayabot"
version = "0.0.1"

repositories {
    mavenLocal()
    maven("https://repo.huaweicloud.com/repository/maven/")
    mavenCentral()
}

val jacksonVersion = "2.9.9"
val lombokVersion = "1.18.8"

dependencies {
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testCompile("org.projectlombok:lombok:$lombokVersion")
    testCompile("org.junit.jupiter:junit-jupiter-api:5.4.2")
    implementation(project(":simplerpc"))

    //序列化
    implementation("com.esotericsoftware:kryo:5.0.0-RC4")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

    //网络协议
    implementation("io.netty:netty-all:4.1.36.Final")
    implementation("com.squareup.okhttp3:okhttp:3.14.2")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}