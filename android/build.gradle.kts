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
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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

tasks.named("preBuild") {
    dependsOn("copySharedJniLibs")
}

val copySharedJniLibs by tasks.registering(Copy::class) {
    val outputDir = layout.buildDirectory.dir("generated/jniLibs/shared")
    val sharedBuildDir = project(":shared").layout.buildDirectory

    dependsOn(
        ":shared:linkReleaseSharedAndroidArm64",
        ":shared:linkReleaseSharedAndroidX64",
        ":shared:linkReleaseSharedAndroidArm32",
        ":shared:linkReleaseSharedAndroidX86",
    )

    from(sharedBuildDir.dir("bin/androidArm64/releaseShared/libshared.so")) {
        into("arm64-v8a")
    }
    from(sharedBuildDir.dir("bin/androidX64/releaseShared/libshared.so")) {
        into("x86_64")
    }
    from(sharedBuildDir.dir("bin/androidArm32/releaseShared/libshared.so")) {
        into("armeabi-v7a")
    }
    from(sharedBuildDir.dir("bin/androidX86/releaseShared/libshared.so")) {
        into("x86")
    }

    into(outputDir)
}
