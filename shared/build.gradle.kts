import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.plugin.mpp.SharedLibrary

plugins {
    alias(libs.plugins.kmm)
    alias(libs.plugins.ks)
}

group = "com.ohyooo"
version = "1.0.0"

kotlin {
    androidNativeArm64("androidArm64")
    androidNativeX64("androidX64")

    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val isMingwX64 = hostOs.startsWith("Windows")
    val isDarwin = hostOs.startsWith("Mac")
    val nativeTarget = when {
        hostOs == "Mac OS X" && isArm64 -> macosArm64()
        hostOs == "Mac OS X" && !isArm64 -> macosX64()
        hostOs == "Linux" && isArm64 -> linuxArm64()
        hostOs == "Linux" && !isArm64 -> linuxX64()
        isMingwX64 -> mingwX64()
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().configureEach {
        binaries {
            sharedLib {
                baseName = "shared"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.io)
                implementation(libs.ktor.client)
                implementation(libs.ktor.cio)
            }
        }
        val androidNativeMain by creating {
            dependsOn(commonMain)
        }
        val androidArm64Main by getting {
            dependsOn(androidNativeMain)
        }
        val androidX64Main by getting {
            dependsOn(androidNativeMain)
        }
        if (isMingwX64) {
            val mingwX64Main by getting {
                dependencies {
                    implementation(libs.ktor.winhttp)
                }
            }
        }
        if (isDarwin) {
            val macosArm64Main by getting {
                dependencies {
                    implementation(libs.ktor.darwin)
                }
            }
            val macosX64Main by getting {
                dependencies {
                    implementation(libs.ktor.darwin)
                }
            }
        }
    }
}

val androidAbiByTarget = mapOf(
    "androidArm64" to "arm64-v8a",
    "androidX64" to "x86_64"
)

tasks.register<Copy>("copyAndroidSharedLibs") {
    val jniLibsDir = rootProject.layout.projectDirectory.dir("android/src/main/jniLibs")
    val kotlinTargets = kotlin.targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>()

    kotlinTargets.toList().filter { it.name in androidAbiByTarget.keys }.forEach { target ->
        val abiDir = androidAbiByTarget.getValue(target.name)
        val sharedLib = target.binaries.withType<SharedLibrary>()
            .single { it.buildType == NativeBuildType.RELEASE }

        dependsOn("linkReleaseShared${target.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}")
        from(sharedLib.outputFile) {
            into(abiDir)
        }
    }

    into(jniLibsDir)
}

