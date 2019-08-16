package test

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.publish.plugins.PublishingPlugin

/**
 * Example class for implementing the Composition Pattern for the
 * MavenPublishPlugin.
 * Exposing new Tasks with the help of MavenPublishPlugin using the
 * PublishExtension of Gradle.
 * @see MavenPublishPlugin
 */
class MavenPublishCompositionPlugin implements Plugin<Project> {

    static final String MAVEN_JAR = 'maven.jar'

    @Override
    void apply(Project project) {
        applyNecessaryPlugins(project)
        setupExtraProperties(project)
        configurePublishingExtension(project)
    }

    /**
     * Configures the PublishingExtension of the Gradle Project
     * @see PublishingPlugin
     * @param project
     */
    private void configurePublishingExtension(Project project) {
        project.publishing {
            repositories {
                if (!project.state.executed) {
                    project.afterEvaluate { p ->
                        mavenCentral()
                    }
                } else {
                    mavenCentral()
                }
            }

            publications {
                module(MavenPublication) {
                    if (!project.state.executed) {
                        project.afterEvaluate { p ->
                            configurePublication(it, p)
                        }
                    } else {
                        configurePublication(it, project)
                    }
                }
            }
        }
    }

    /**
     * Applies plugins necessary for the MavenPublishCompositionPlugin to run.
     * @see MavenPublishCompositionPlugin
     * @param project to configure
     */
    private void applyNecessaryPlugins(Project project) {
        project.plugins.apply(PublishingPlugin)
        project.plugins.apply(MavenPublishPlugin)
    }

    /**
     * Setup Extra Properties for the project. Necessary to determine if
     * JavaPlugin is present
     * @param project to set the extra properties in
     */
    private void setupExtraProperties(Project project) {
        project.ext.set(MAVEN_JAR, false)
        project.plugins.withType(JavaPlugin) {
            project.ext.set(MAVEN_JAR, true)
        }
    }

    /**
     * Adds a new Publication if JavaPlugin is present
     * @param publication to configure
     * @param project of the project to receive the components from
     */
    private void configurePublication(MavenPublication publication, Project project) {
        if (project.ext.get(MAVEN_JAR)) {
            publication.from project.components.java
        }
    }
}
