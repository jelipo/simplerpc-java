
apply(plugin = "eclipse")
apply(plugin = "java")

group = "com.mayabot"
version = "0.0.1"

repositories {
    mavenLocal()
    maven("https://repo.huaweicloud.com/repository/maven/")
    mavenCentral()
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
    compileOnly(project(":com.springmarker.simplerpc-netty"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}