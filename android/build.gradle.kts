plugins {
    alias(libs.plugins.agp)
    alias(libs.plugins.ks)
    alias(libs.plugins.cc)
    alias(libs.plugins.jc)
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("signkey.jks")
            storePassword = "123456"
            keyPassword = "123456"
            keyAlias = "demo"

            enableV3Signing = true
            enableV4Signing = true
        }
    }
    namespace = "com.ohyooo.demo"

    compileSdk {
        version = release(libs.versions.compile.sdk.get().toInt()) {
            minorApiLevel = libs.versions.compile.minor.get().toInt()
        }
    }

    defaultConfig {
        applicationId = "com.ohyooo.demo"
        minSdk = libs.versions.min.sdk.get().toInt()
        targetSdk = libs.versions.target.sdk.get().toInt()
        versionCode = 1
        versionName = "1.0" // +rootProject.extra["gitVersion"]
        proguardFile("proguard-rules.pro")
        signingConfig = signingConfigs.getByName("debug")
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            ndk {
                debugSymbolLevel = "NONE"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        // Disable unused AGP features
        buildConfig = false
        aidl = false
        resValues = false
        shaders = false
    }
    sourceSets {
        getByName("main") {
            jniLibs.srcDir(file("$buildDir/generated/jniLibs/shared"))
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.foundation)
}

val sharedBuildDir = project(":shared").layout.buildDirectory
val sharedOutputDir = layout.buildDirectory.dir("generated/jniLibs/shared")

fun registerCopySharedJniLibs(taskName: String, buildTypeName: String) =
    tasks.register(taskName, Copy::class) {
        val sharedDir = if (buildTypeName == "debug") "debugShared" else "releaseShared"
        val linkPrefix = if (buildTypeName == "debug") "linkDebugShared" else "linkReleaseShared"

        dependsOn(
            ":shared:${linkPrefix}AndroidArm64",
            ":shared:${linkPrefix}AndroidX64",
            ":shared:${linkPrefix}AndroidArm32",
            ":shared:${linkPrefix}AndroidX86",
        )

        from(sharedBuildDir.dir("bin/androidArm64/$sharedDir/libshared.so")) {
            into("arm64-v8a")
        }
        from(sharedBuildDir.dir("bin/androidX64/$sharedDir/libshared.so")) {
            into("x86_64")
        }
        from(sharedBuildDir.dir("bin/androidArm32/$sharedDir/libshared.so")) {
            into("armeabi-v7a")
        }
        from(sharedBuildDir.dir("bin/androidX86/$sharedDir/libshared.so")) {
            into("x86")
        }

        into(sharedOutputDir)
    }

val copySharedJniLibsDebug = registerCopySharedJniLibs("copySharedJniLibsDebug", "debug")
val copySharedJniLibsRelease = registerCopySharedJniLibs("copySharedJniLibsRelease", "release")

tasks.matching { it.name == "mergeDebugJniLibFolders" }.configureEach {
    dependsOn(copySharedJniLibsDebug)
}
tasks.matching { it.name == "mergeReleaseJniLibFolders" }.configureEach {
    dependsOn(copySharedJniLibsRelease)
}
