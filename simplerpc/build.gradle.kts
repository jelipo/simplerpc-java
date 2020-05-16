plugins {
    java
    application
    `maven-publish`
}


version = "0.0.3"

val lombokVersion = "1.18.12"

dependencies {
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("com.google.guava:guava:29.0-jre")
    implementation("cglib:cglib:3.3.0")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    //用于扫描注解
    implementation("org.reflections:reflections:0.9.12")
    //序列化
    compileOnly("com.esotericsoftware:kryo:5.0.0-RC5")
    //网络协议
    compileOnly("io.netty:netty-all:4.1.50.Final")
    //lombok
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
}
java {
    withSourcesJar()
}
publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            pom {
                version = System.getenv("GITHUB_SHA")?:"local12".substring(0, 6)
            }
        }
    }
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/jelipo/simplerpc-java")
            credentials {
                username = System.getenv("USERNAME") ?: ""
                password = System.getenv("TOKEN") ?: ""
            }
        }
    }
}