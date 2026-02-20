import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.konan.target.Architecture.ARM32
import org.jetbrains.kotlin.konan.target.Architecture.ARM64
import org.jetbrains.kotlin.konan.target.Architecture.X64
import org.jetbrains.kotlin.konan.target.Architecture.X86
import org.jetbrains.kotlin.konan.target.Family.ANDROID

plugins {
    alias(libs.plugins.kmm)
    alias(libs.plugins.alp)
    alias(libs.plugins.ks)
}

group = "com.ohyooo"
version = "1.0.0"

kotlin {
    androidLibrary {
        namespace = "com.ohyooo.demo.shared"
        compileSdk = libs.versions.compile.sdk.get().toInt()
        minSdk = libs.versions.min.sdk.get().toInt()
    }

    val hostOs = System.getProperty("os.name")
    val isDarwin = hostOs.startsWith("Mac")
    val hasXcode = isDarwin && (
            System.getenv("DEVELOPER_DIR")?.contains("Xcode.app") == true ||
                    File("/Applications/Xcode.app").exists()
            )
    val linuxTargets = listOf(
        linuxArm64(),
        linuxX64(),
    )
    val androidNativeTargets = listOf(
        androidNativeArm32(),
        androidNativeArm64(),
        androidNativeX86(),
        androidNativeX64(),
    )
    val windowsTargets = listOf(
        mingwX64(),
    )
    val appleTargets: List<KotlinNativeTarget> = if (hasXcode) {
        listOf(
            iosArm64(),
            iosSimulatorArm64(),
            macosArm64(),
            macosX64(),
            tvosArm64(),
            tvosSimulatorArm64(),
            tvosX64(),
            watchosArm32(),
            watchosArm64(),
            watchosDeviceArm64(),
            watchosSimulatorArm64(),
            watchosX64(),
        )
    } else {
        emptyList()
    }

    if (hasXcode) {
        val sharedXCFramework = XCFramework("shared")
        appleTargets
            .filter { it.name == "iosArm64" || it.name == "iosSimulatorArm64" }
            .forEach { target ->
            target.binaries.framework {
                baseName = "shared"
                isStatic = true
                binaryOption("bundleId", "com.ohyooo.demo.shared")
                sharedXCFramework.add(this)
            }
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
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
                implementation(libs.kotlinx.io)
                implementation(libs.ktor.client)
            }
        }
        val androidMain = maybeCreate("androidMain").apply {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.ktor.okhttp)
            }
        }
        val nativeMain = maybeCreate("nativeMain").apply {
            dependsOn(commonMain)
        }
        val androidNativeMain = maybeCreate("androidNativeMain").apply {
            dependsOn(nativeMain)
            dependencies {
                implementation(libs.ktor.cio)
            }
        }
        val posixMain = maybeCreate("posixMain").apply {
            dependsOn(nativeMain)
            dependencies {
                implementation(libs.ktor.cio)
            }
        }
        val mingwMain = maybeCreate("mingwMain").apply {
            dependsOn(nativeMain)
            dependencies {
                implementation(libs.ktor.winhttp)
            }
        }
        val linuxMain = maybeCreate("linuxMain").apply {
            dependsOn(posixMain)
        }

        linuxTargets.forEach { target ->
            getByName("${target.name}Main").dependsOn(linuxMain)
        }
        androidNativeTargets.forEach { target ->
            getByName("${target.name}Main").dependsOn(androidNativeMain)
        }
        windowsTargets.forEach { target ->
            getByName("${target.name}Main").dependsOn(mingwMain)
        }

        if (hasXcode) {
            val appleMain = maybeCreate("appleMain").apply {
                dependsOn(nativeMain)
                dependencies {
                    implementation(libs.ktor.darwin)
                }
            }
            val iosMain = maybeCreate("iosMain").apply {
                dependsOn(appleMain)
            }
            val macosMain = maybeCreate("macosMain").apply {
                dependsOn(appleMain)
            }
            val tvosMain = maybeCreate("tvosMain").apply {
                dependsOn(appleMain)
            }
            val watchosMain = maybeCreate("watchosMain").apply {
                dependsOn(appleMain)
            }

            appleTargets.forEach { target ->
                val targetMain = getByName("${target.name}Main")
                when {
                    target.name.startsWith("ios") -> targetMain.dependsOn(iosMain)
                    target.name.startsWith("macos") -> targetMain.dependsOn(macosMain)
                    target.name.startsWith("tvos") -> targetMain.dependsOn(tvosMain)
                    target.name.startsWith("watchos") -> targetMain.dependsOn(watchosMain)
                }
            }
        }
    }
}

fun String.uppercaseFirst(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

fun registerLinkAllSharedTask(taskName: String, taskPrefix: String, descriptionText: String) =
    tasks.register(taskName) {
        group = "verification"
        description = descriptionText

        kotlin.targets.withType<KotlinNativeTarget>().forEach { target ->
            dependsOn("$taskPrefix${target.name.uppercaseFirst()}")
        }
    }

val linkDebugSharedAllTargets = registerLinkAllSharedTask(
    taskName = "linkDebugSharedAllTargets",
    taskPrefix = "linkDebugShared",
    descriptionText = "Links debug shared libraries for all configured Kotlin/Native targets.",
)

val linkReleaseSharedAllTargets = registerLinkAllSharedTask(
    taskName = "linkReleaseSharedAllTargets",
    taskPrefix = "linkReleaseShared",
    descriptionText = "Links release shared libraries for all configured Kotlin/Native targets.",
)

tasks.register("ciBuildSharedAllTargets") {
    group = "verification"
    description = "Links debug and release shared libraries for all configured Kotlin/Native targets."
    dependsOn(linkDebugSharedAllTargets)
    dependsOn(linkReleaseSharedAllTargets)
}

// Backward-compatible aliases for old Android Native task names.
tasks.register("linkDebugSharedAndroidArm64") {
    dependsOn("linkDebugSharedAndroidNativeArm64")
}
tasks.register("linkDebugSharedAndroidX64") {
    dependsOn("linkDebugSharedAndroidNativeX64")
}
tasks.register("linkDebugSharedAndroidArm32") {
    dependsOn("linkDebugSharedAndroidNativeArm32")
}
tasks.register("linkDebugSharedAndroidX86") {
    dependsOn("linkDebugSharedAndroidNativeX86")
}
tasks.register("linkReleaseSharedAndroidArm64") {
    dependsOn("linkReleaseSharedAndroidNativeArm64")
}
tasks.register("linkReleaseSharedAndroidX64") {
    dependsOn("linkReleaseSharedAndroidNativeX64")
}
tasks.register("linkReleaseSharedAndroidArm32") {
    dependsOn("linkReleaseSharedAndroidNativeArm32")
}
tasks.register("linkReleaseSharedAndroidX86") {
    dependsOn("linkReleaseSharedAndroidNativeX86")
}
