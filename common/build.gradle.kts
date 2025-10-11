plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
}
//apply(from = "genApplicationVersion.gradle.kts")

group = "com.github.Yyaduo"
version = "1.0.11"

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
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


//tasks.register<Jar>("sourcesJar") {
//    archiveClassifier.set("sources")
//    from(android.sourceSets["main"].kotlin.srcDirs())
////    from(android.sourceSets["main"].java.srcDirs)
//}

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



// 发布配置 - 必须放在afterEvaluate块中确保Android配置已完成
//afterEvaluate {
//    publishing {
//        publications {
//            create<MavenPublication>("release") {
//                // 关联Android库的发布组件
//                from(components["release"])
//
//                // 配置Maven坐标
//                groupId = "com.github.Yyaduo"
//                artifactId = "YaduoCommon"
//                version = "1.0.10"
//
//                // 配置源码Jar
//                artifact(tasks.register<Jar>("sourcesJar") {
//                    archiveClassifier.set("sources")
//                    from(android.sourceSets["main"].java.srcDirs)
////                    from(android.sourceSets["main"].kotlin.srcDirs)
//                })
//
//                // 配置文档Jar
//                artifact(tasks.register<Jar>("javadocJar") {
//                    archiveClassifier.set("javadoc")
//                    dependsOn("javadoc")
//                    from(tasks.named("javadoc").get().outputs)
//                })
//            }
//        }
//
//        // 可选：配置发布仓库
//        repositories {
//            maven {
//                name = "GitHubPackages"
//                url = uri("https://maven.pkg.github.com/Yyaduo/YaduoCommon")
//                credentials {
//                    username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
//                    password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
//                }
//            }
//        }
//    }
//
//    // Javadoc配置
////    tasks.javadoc {
////        source = android.sourceSets["main"].java.srcDirs + android.sourceSets["main"].kotlin.srcDirs
////        classpath += project.files(android.bootClasspath.joinToString(File.pathSeparator))
////        options {
////            this as StandardJavadocDocletOptions
////            encoding = "UTF-8"
////            charSet = "UTF-8"
////            links("https://developer.android.com/reference/")
////            links("https://docs.oracle.com/javase/8/docs/api/")
////            addStringOption("Xdoclint:none", "-quiet")
////        }
////    }
//}