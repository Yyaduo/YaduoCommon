plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
}
//apply(from = "genApplicationVersion.gradle.kts")
group = "com.github.Yyaduo"
version = "1.0.9"

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                artifact(tasks["sourcesJar"]) // 添加源码JAR

                groupId = (group.toString())
                artifactId = "YaduoCommon"
                version = version
            }
        }
    }
}

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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

//    sourceSets {
//        getByName("main") {
//            java.srcDirs("src/main/java")
//            kotlin.srcDirs("src/main/kotlin")
//        }
//    }
}

//tasks.register("genApplicationVersion") {
//    doLast {
//        // 生成文件的预期路径
//        val expectedPath =
//            "${project.buildDir}/generated/source/version/com/yaduo/common/ApplicationVersion.kt"
//        val file = File(expectedPath)
//        if (file.exists()) {
//            println("✅ ApplicationVersion.kt 生成成功: $expectedPath")
//        } else {
//            println("❌ 错误: ApplicationVersion.kt 未生成，预期路径: $expectedPath")
//        }
//    }
//
//    val oldVersionFile =
//        File("${project.projectDir.absolutePath}/src/com/yaduo/common/ApplicationVersion.kt")
//    if (oldVersionFile.exists()) oldVersionFile.delete()
//
//    val namespace = android.namespace ?: "com.yaduo.common"
//    // 调用 genVersionFile 函数
//    @Suppress("UNCHECKED_CAST")
//    (rootProject.extra["genVersionFile"] as (Project, String) -> Unit).invoke(
//        project,
//        namespace
//    )
//}


tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].kotlin.srcDirs())
//    from(android.sourceSets["main"].java.srcDirs)
}

//project.afterEvaluate {
//    // 确保preBuild依赖genApplicationVersion
//    tasks.named("preBuild") {
//        dependsOn("genApplicationVersion")
//    }
//
//    // 在评估完成后再获取compileReleaseKotlin任务，确保任务已存在
//    tasks.findByName("compileReleaseKotlin")?.dependsOn("genApplicationVersion")
//    // 同时添加debug版本的依赖，确保全版本兼容
//    tasks.findByName("compileDebugKotlin")?.dependsOn("genApplicationVersion")
//}


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

//afterEvaluate {
//    publishing {
//        publications {
//            create<MavenPublication>("release") {
//                from(components["release"])
//                artifact(tasks["sourcesJar"]) // 添加源码JAR
//
//                // 设置Maven坐标
//                groupId = group.toString()
//                artifactId = "YaduoCommon"
//                version = version
//            }
//        }
//
//        // 为元数据生成任务添加对sourcesJar的依赖
//        tasks.named("generateMetadataFileForReleasePublication") {
//            dependsOn("sourcesJar")
//        }
//    }
//}
