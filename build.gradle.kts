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
        jdkName = "11"
        languageLevel = IdeaLanguageLevel("11")
        vcs = "Git"
    }
}
