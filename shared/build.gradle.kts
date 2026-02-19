import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kmm)
    alias(libs.plugins.alp)
    alias(libs.plugins.ks)
    alias(libs.plugins.cc)
    alias(libs.plugins.jc)
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

    if (hasXcode) {
        val sharedXCFramework = XCFramework("shared")
        listOf(
            iosArm64(),
            iosX64(),
            iosSimulatorArm64(),
        ).forEach { target ->
            target.binaries.framework {
                baseName = "shared"
                isStatic = true
                sharedXCFramework.add(this)
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
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.io)
                implementation(libs.ktor.client)
                implementation(libs.ktor.cio)
            }
        }
        if (hasXcode) {
            val iosMain by creating {
                dependsOn(commonMain)
                dependencies {
                    implementation(libs.ktor.darwin)
                }
            }
            val iosArm64Main by getting {
                dependsOn(iosMain)
            }
            val iosX64Main by getting {
                dependsOn(iosMain)
            }
            val iosSimulatorArm64Main by getting {
                dependsOn(iosMain)
            }
        }
    }
}
