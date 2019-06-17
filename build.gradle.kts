import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel

plugins {
    java
    application
    idea
}

group = "com.springmarker"

tasks {
    compileJava { options.encoding = "UTF-8" }
    javadoc { options.encoding = "UTF-8" }
}

idea {
    project {
        jdkName = "1.8"
        languageLevel = IdeaLanguageLevel("1.8")
        vcs = "Git"
    }
}
