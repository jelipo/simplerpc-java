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
    implementation("ch.qos.logback:logback-classic:1.2.3")

    compile("com.github.ben-manes.caffeine:caffeine:2.7.0")
    implementation("cglib:cglib:3.2.12")

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testCompile("org.junit.jupiter:junit-jupiter-api:5.4.2")

    //序列化
    compileOnly("com.esotericsoftware:kryo:5.0.0-RC4")
    compileOnly("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    compileOnly("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

    //网络协议
    compileOnly("io.netty:netty-all:4.1.36.Final")
    compileOnly("com.squareup.okhttp3:okhttp:3.14.2")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}