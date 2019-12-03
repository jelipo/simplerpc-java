import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel

plugins {
    java
    application
    idea
}

group = "com.Jelipo"

tasks {
    compileJava { options.encoding = "UTF-8" }
    javadoc { options.encoding = "UTF-8" }
}

idea.project {
    jdkName = "1.8"
    languageLevel = IdeaLanguageLevel("8")
    vcs = "Git"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}