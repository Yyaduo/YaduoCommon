plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
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
            kotlin.srcDirs(layout.buildDirectory.dir("generated/source/version"))
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
    doLast {
        // 生成文件的预期路径
        val expectedPath = "${project.buildDir}/generated/source/version/com/yaduo/common/ApplicationVersion.kt"
        val file = File(expectedPath)
        if (file.exists()) {
            println("✅ ApplicationVersion.kt 生成成功: $expectedPath")
        } else {
            println("❌ 错误: ApplicationVersion.kt 未生成，预期路径: $expectedPath")
        }
    }

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


task("sourcesJar", Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].java.srcDirs)
}

project.afterEvaluate {
    // 确保preBuild依赖genApplicationVersion
    tasks.named("preBuild") {
        dependsOn("genApplicationVersion")
    }

    // 在评估完成后再获取compileReleaseKotlin任务，确保任务已存在
    tasks.findByName("compileReleaseKotlin")?.dependsOn("genApplicationVersion")
    // 同时添加debug版本的依赖，确保全版本兼容
    tasks.findByName("compileDebugKotlin")?.dependsOn("genApplicationVersion")
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

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                artifact(tasks["sourcesJar"]) // 添加源码JAR
                // 设置Maven坐标
                groupId = "com.github.Yyaduo"
                artifactId = "YaduoCommon"
                version = "1.0.0"
            }
        }
    }
}
