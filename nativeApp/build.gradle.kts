plugins {
    alias(libs.plugins.kmm)
}

group = "com.ohyooo"
version = "1.0.0"

kotlin {
    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"

    val nativeTarget = when {
        hostOs == "Mac OS X" && isArm64 -> macosArm64("native")
        hostOs == "Mac OS X" && !isArm64 -> macosX64("native")
        hostOs == "Linux" && isArm64 -> linuxArm64("native")
        hostOs == "Linux" && !isArm64 -> linuxX64("native")
        hostOs.startsWith("Windows") -> mingwX64("native")
        else -> error("Host OS is not supported: $hostOs")
    }

    nativeTarget.binaries {
        executable {
            baseName = "native-demo"
            entryPoint = "main"
        }
    }
}
