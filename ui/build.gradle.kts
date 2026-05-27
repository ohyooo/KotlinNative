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
    android {
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
    val appleTargets: List<KotlinNativeTarget> = if (hasXcode) {
        listOf(
            iosArm64(),
            iosSimulatorArm64(),
            macosArm64(),
        )
    } else {
        emptyList()
    }

    if (hasXcode) {
        val uiXCFramework = XCFramework("ui")
        appleTargets.forEach { target ->
            target.binaries.framework {
                baseName = "ui"
                isStatic = true
                binaryOption("bundleId", "com.ohyooo.demo.ui")
                export(project(":shared"))
                uiXCFramework.add(this)
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material)
                implementation(libs.compose.ui)
                implementation(libs.kotlinx.coroutines.core)
                api(project(":shared"))
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
        }
        if (hasXcode) {
            val iosMain = maybeCreate("iosMain").apply {
                dependsOn(commonMain)
            }
            val macosMain = maybeCreate("macosMain").apply {
                dependsOn(commonMain)
            }
            appleTargets.forEach { target ->
                val targetMain = getByName("${target.name}Main")
                when {
                    target.name.startsWith("ios") -> targetMain.dependsOn(iosMain)
                    target.name.startsWith("macos") -> targetMain.dependsOn(macosMain)
                }
            }
        }
    }
}
