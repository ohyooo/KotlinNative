import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.konan.target.Architecture.ARM32
import org.jetbrains.kotlin.konan.target.Architecture.ARM64
import org.jetbrains.kotlin.konan.target.Architecture.X64
import org.jetbrains.kotlin.konan.target.Architecture.X86
import org.jetbrains.kotlin.konan.target.Family.ANDROID

plugins {
    alias(libs.plugins.kmm)
    alias(libs.plugins.ks)
}

group = "com.ohyooo"
version = "1.0.0"

kotlin {
    androidNativeArm64("androidArm64")
    androidNativeX64("androidX64")
    androidNativeArm32("androidArm32")
    androidNativeX86("androidX86")

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

                val isDebug = buildType == NativeBuildType.DEBUG

                if (isDebug) {
                    freeCompilerArgs += "-g"
                }

                if (!isDebug) {
                    freeCompilerArgs += "-opt"
                }

                debuggable = isDebug
                optimized = !isDebug

                binaryOptions["smallBinary"] = (!isDebug).toString()

                if (konanTarget.family == ANDROID && konanTarget.architecture in arrayOf(ARM32, ARM64, X86, X64)) {
                    linkerOpts("-Wl,-z,max-page-size=16384")
                    linkerOpts("-Wl,-z,common-page-size=16384")
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.coroutines.core)
//                implementation(libs.kotlinx.io)
//                implementation(libs.ktor.client)
//                implementation(libs.ktor.cio)
            }
        }
        val androidNativeMain by creating {
            dependsOn(commonMain)
        }
        val androidArm64Main by getting {
            dependsOn(androidNativeMain)
        }
        val androidArm32Main by getting {
            dependsOn(androidNativeMain)
        }
        val androidX64Main by getting {
            dependsOn(androidNativeMain)
        }
        val androidX86Main by getting {
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


