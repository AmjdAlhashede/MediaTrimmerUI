// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}


group = libs.versions.libGroup.get()
version = libs.versions.libVersion.get()

nexusPublishing {
    repositories {
        // see https://central.sonatype.org/publish/publish-portal-ossrh-staging-api/#configuration
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
            username.set(project.findProperty("ossrhUsername") as String?)
            password.set(project.findProperty("ossrhPassword") as String?)
        }
    }
}
