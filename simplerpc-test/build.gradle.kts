plugins {
    java
    application
}

version = "0.0.1"

tasks {
    jar {
        manifest {
            attributes["Implementation-Title"] = "SimpleRPC-Java"
            attributes["Implementation-Version"] = archiveVersion
            attributes["Main-Class"] = "com.jelipo.rpctest.Main"
        }
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    }
}

val lombokVersion = "1.18.10"

dependencies {
    implementation("com.google.guava:guava:28.2-jre")
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testImplementation("org.projectlombok:lombok:$lombokVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    implementation(project(":simplerpc"))
    //序列化
    implementation("com.esotericsoftware:kryo:5.0.0-RC4")
    //网络协议
    implementation("io.netty:netty-all:4.1.45.Final")
}
