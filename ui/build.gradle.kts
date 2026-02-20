import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kmm)
    alias(libs.plugins.alp)
    alias(libs.plugins.cc)
    alias(libs.plugins.jc)
}

group = "com.ohyooo"
version = "1.0.0"

kotlin {
    androidLibrary {
        namespace = "com.ohyooo.demo.ui"
        compileSdk = libs.versions.compile.sdk.get().toInt()
        minSdk = libs.versions.min.sdk.get().toInt()
    }

    val hostOs = System.getProperty("os.name")
    val isDarwin = hostOs.startsWith("Mac")
    val hasXcode = isDarwin && (
            System.getenv("DEVELOPER_DIR")?.contains("Xcode.app") == true ||
                    File("/Applications/Xcode.app").exists()
            )
    val iosTargets: List<KotlinNativeTarget> = if (hasXcode) {
        listOf(
            iosArm64(),
            iosSimulatorArm64(),
        )
    } else {
        emptyList()
    }

    if (hasXcode) {
        val uiXCFramework = XCFramework("ui")
        iosTargets.forEach { target ->
            target.binaries.framework {
                baseName = "ui"
                isStatic = true
                binaryOption("bundleId", "com.ohyooo.demo.ui")
                uiXCFramework.add(this)
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
        }
        if (hasXcode) {
            val iosMain = maybeCreate("iosMain").apply {
                dependsOn(commonMain)
            }
            iosTargets.forEach { target ->
                getByName("${target.name}Main").dependsOn(iosMain)
            }
        }
    }
}
