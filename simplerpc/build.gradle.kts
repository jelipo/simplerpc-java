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

    implementation("com.google.guava:guava:28.0-jre")
    implementation("cglib:cglib:3.2.12")

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testCompile("org.junit.jupiter:junit-jupiter-api:5.4.2")
    //用于扫描注解
    compile("org.reflections:reflections:0.9.11")


    //序列化
    compileOnly("com.esotericsoftware:kryo:5.0.0-RC4")

    //网络协议
    compileOnly("io.netty:netty-all:4.1.36.Final")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
