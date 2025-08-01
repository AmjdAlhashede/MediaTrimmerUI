plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
    signing
}

group = libs.versions.libGroup.get()
version = libs.versions.libVersion.get()

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

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = libs.versions.libGroup.get()
                artifactId = "mediatrimmer-compose-ui"
                version = libs.versions.libVersion.get()

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
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/AmjdAlhashede/MediaTrimmerUI")
                credentials {
                    username = project.findProperty("gpr.user") as String?
                    password = project.findProperty("gpr.key") as String?
                }
            }
        }
    }

    signing {
        useGpgCmd()
        sign(publishing.publications["release"])
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)

    compileOnly(libs.androidx.media3.exoplayer)
}



