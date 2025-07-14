plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
    signing
}


group = "io.github.amjdalhashede"
version = "1.0.0-alpha"

android {
    namespace = "io.github.amjdalhashede.mediatrimmer.compose.ui"
    compileSdk = 36

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    compileOnly(libs.bundles.media3.local)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}


afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "io.github.amjdalhashede"
                artifactId = "mediatrimmer-compose-ui"
                version = "1.0.0-alpha"

                pom {
                    name.set("MediaTrimmer Compose UI")
                    description.set("A Jetpack Compose UI library for media trimming with advanced controls and features.")
                    url.set("https://github.com/AmjdAlhashede/MediaTrimmerUI")

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }

                    developers {
                        developer {
                            id.set("Amjdalhashede")
                            name.set("Amjd Alhashede")
                            url.set("https://github.com/AmjdAlhashede")
                        }
                    }

                    scm {
                        connection.set("scm:git:git://github.com/AmjdAlhashede/MediaTrimmerUI.git")
                        developerConnection.set("scm:git:ssh://git@github.com/AmjdAlhashede/MediaTrimmerUI.git")
                        url.set("https://github.com/AmjdAlhashede/MediaTrimmerUI")
                    }
                }
            }
        }

        repositories {
            maven {
                name = "myrepo"
                url = uri(layout.buildDirectory.dir("repo"))
            }
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/AmjdAlhashede/MediaTrimmerUI") // ✅ هذا هو التعديل الصحيح
                credentials {
                    username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME_GITHUB")
                    password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN_GITHUB")
                }
            }
            maven {
                name = "Sonatype"
                url = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
//                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
//                url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                credentials {
                    username = project.findProperty("ossrhUsername") as String?
                    password = project.findProperty("ossrhPassword") as String?
                }
            }
        }
    }

    signing {
        useGpgCmd()
        sign(publishing.publications["release"])
    }
}

tasks.register<Zip>("generateRepoZip") {
    dependsOn("publishReleasePublicationToMyrepoRepository")

    from(layout.buildDirectory.dir("repo"))

    archiveFileName.set("mediatrimmer-compose-ui-1.0.0-alpha.zip")
    destinationDirectory.set(layout.buildDirectory.dir("outputs"))
}