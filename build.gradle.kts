import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel

plugins {
    java
    idea
}

allprojects {
    apply(plugin = "java")

    group = "com.jelipo"

    repositories {
        mavenLocal()
        maven("https://repo.huaweicloud.com/repository/maven/")
        mavenCentral()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    tasks {
        compileJava { options.encoding = "UTF-8" }
        javadoc { options.encoding = "UTF-8" }
    }
}

idea {
    project {
        jdkName = "1.8"
        languageLevel = IdeaLanguageLevel("1.8")
        targetVersion = "1.8"
        targetBytecodeVersion = JavaVersion.VERSION_1_8
        vcs = "Git"
    }
    targetVersion = "1.8"
}

