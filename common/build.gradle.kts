plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}
apply(from = "genApplicationVersion.gradle.kts")
android {
    namespace = "com.yaduo.common"
    compileSdk = rootProject.extra["compileSdk"] as Int

    defaultConfig {
        minSdk = rootProject.extra["minSdk"] as Int

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    sourceSets {
        getByName("main") {
            java.srcDirs(layout.buildDirectory.dir("generated/source/version"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.register("genApplicationVersion") {
    val oldVersionFile =
        File("${project.projectDir.absolutePath}/src/com/yaduo/common/ApplicationVersion.kt")
    if (oldVersionFile.exists()) oldVersionFile.delete()

    val namespace = android.namespace ?: "com.yaduo.common"
    // 调用 genVersionFile 函数
    @Suppress("UNCHECKED_CAST")
    (rootProject.extra["genVersionFile"] as (Project, String) -> Unit).invoke(
        project,
        namespace
    )
}

project.afterEvaluate {
    tasks.named("preBuild") {
        dependsOn("genApplicationVersion")
    }
}


dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.material)
    implementation(libs.crashreport)
    implementation(libs.chucker)
    implementation(libs.androidautosize)
    api(libs.eventbus)
    implementation(libs.okhttp)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}