plugins {
    alias(libs.plugins.kmm)
}

group = "com.ohyooo"
version = "1.0.0"

kotlin {
    val nativeTargets = listOf(
        androidNativeArm32(),
        androidNativeArm64(),
        androidNativeX86(),
        androidNativeX64(),
        iosArm64(),
        iosSimulatorArm64(),
        iosX64(),
        macosArm64(),
        macosX64(),
        linuxArm64(),
        linuxX64(),
        mingwX64(),
        tvosArm64(),
        tvosSimulatorArm64(),
        tvosX64(),
        watchosArm32(),
        watchosArm64(),
        watchosDeviceArm64(),
        watchosSimulatorArm64(),
        watchosX64(),
    )

    nativeTargets.forEach { target ->
        target.binaries {
            executable {
                // Keep artifacts unique for release uploads across targets.
                baseName = "native-demo-${target.name}"
                entryPoint = "main"
            }
        }
    }

    sourceSets {
        val commonMain by getting
        val nativeMain = maybeCreate("nativeMain").apply {
            kotlin.srcDir("src/nativeMain/kotlin")
            dependsOn(commonMain)
        }
        nativeTargets.forEach { target ->
            getByName("${target.name}Main").dependsOn(nativeMain)
        }
    }
}
