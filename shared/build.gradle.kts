plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().all {
        compilations.getByName("main") {
            cinterops.create("FirebaseCrashlytics") {
                includeDirs("$projectDir/src/nativeInterop/cinterop/FirebaseCrashlytics")
                compilerOpts("-DNS_FORMAT_ARGUMENT(A)=", "-D_Nullable_result=_Nullable")
            }
        }
    }

    // Without this it will throw: Undefined symbols for architecture arm64
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>()
        .map { target ->
            val mainCompilation = target.compilations.getByName("main")
            val dynamicFrameworks =
                target.binaries.filterIsInstance<org.jetbrains.kotlin.gradle.plugin.mpp.Framework>()
                    .filter { framework -> !framework.isStatic }

            Pair(mainCompilation, dynamicFrameworks)
        }
        .forEach { (compilation, frameworks) ->
            if (frameworks.isNotEmpty()) {
                compilation.kotlinOptions.freeCompilerArgs += listOf(
                    "-linker-options",
                    "-U _FIRCLSExceptionRecordNSException " +
                            "-U _OBJC_CLASS_\$_FIRStackFrame " +
                            "-U _OBJC_CLASS_\$_FIRExceptionModel " +
                            "-U _OBJC_CLASS_\$_FIRCrashlytics",
                )
            }
        }
}

android {
    namespace = "com.example.crashycrash"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
    }
}