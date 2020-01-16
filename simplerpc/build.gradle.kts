plugins {
    java
    application
}

version = "0.0.2"

val lombokVersion = "1.18.10"

dependencies {
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("com.google.guava:guava:28.2-jre")
    implementation("cglib:cglib:3.3.0")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    //用于扫描注解
    implementation("org.reflections:reflections:0.9.12")
    //序列化
    compileOnly("com.esotericsoftware:kryo:5.0.0-RC4")
    //网络协议
    compileOnly("io.netty:netty-all:4.1.45.Final")
    //lombok
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
}
