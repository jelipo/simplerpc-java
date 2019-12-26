plugins {
    java
    application
}

group = "com.jelipo"
version = "0.0.1"

repositories {
    mavenLocal()
    maven("https://repo.huaweicloud.com/repository/maven/")
    mavenCentral()
}
java {

}

tasks {
    jar {
        manifest {
            attributes["Implementation-Title"] = "Gradle Jar File Example"
            attributes["Implementation-Version"] = version
            attributes["Main-Class"] = "com.jelipo.rpctest.Main"
        }
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    }
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

    //网络协议
    implementation("io.netty:netty-all:4.1.36.Final")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

