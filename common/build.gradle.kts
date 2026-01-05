import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
}

android {
    namespace = "com.yaduo.common"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
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

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar() // 可选：发布源码
            withJavadocJar() // 可选：发布文档
        }
    }

}

afterEvaluate {
    // 识别 JitPack 传递的 skipPublishCheck 参数，跳过认证校验
    val skipPublishCheck = project.hasProperty("skipPublishCheck") ||
            System.getenv("SKIP_PUBLISH_CHECK") == "true"
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "com.github.Yyaduo"
                artifactId = "common"
                version = project.property("VERSION_NAME").toString()
            }
        }

        repositories {
            // 仅在「不跳过校验」且「明确指定发布到GitHub Packages」时，才配置仓库+校验认证
            val isPublishToGitHubPackages = project.hasProperty("publishToGitHubPackages")
            if (!skipPublishCheck && isPublishToGitHubPackages) {
                maven {
                    name = "YaduoCommon"
                    url = uri("https://maven.pkg.github.com/Yyaduo/YaduoCommon")

                    credentials {
                        var username: String? = null
                        var password: String? = null

                        // 优先读取环境变量
                        username = System.getenv("PUBLISH_MAVEN_USER")
                        password = System.getenv("PUBLISH_MAVEN_KEY")
                        println("📌 从环境变量读取认证信息：用户名 = $username，密钥长度 = ${password?.length ?: 0}")

                        // 读取本地local.properties（本地开发用）
                        if (username.isNullOrBlank() || password.isNullOrBlank()) {
                            val localPropsFile = project.rootProject.file("local.properties")
                            if (localPropsFile.exists()) {
                                val localProps = Properties()
                                localProps.load(FileInputStream(localPropsFile))
                                username = localProps.getProperty("PUBLISH_MAVEN_USER")
                                password = localProps.getProperty("PUBLISH_MAVEN_KEY")
                                println("📌 从local.properties读取认证信息：用户名 = $username，密钥长度 = ${password?.length ?: 0}")
                            }
                        }

                        // 兜底读取Project属性（GitHub Actions用，通过-P参数传递）
                        if (username.isNullOrBlank() || password.isNullOrBlank()) {
                            username = project.findProperty("publish.user")?.toString()
                            password = project.findProperty("publish.key")?.toString()
                            println("📌 从Project属性读取认证信息：用户名 = $username，密钥长度 = ${password?.length ?: 0}")
                        }

                        // 严格空值校验 + 明确错误提示
                        if (username.isNullOrBlank()) {
                            throw GradleException(
                                "❌ 发布认证用户名为空！\n" +
                                        "本地开发：请在local.properties中配置PUBLISH_MAVEN_USER\n" +
                                        "CI/CD：请通过-Ppublish.user传递GitHub用户名"
                            )
                        }
                        if (password.isNullOrBlank()) {
                            throw GradleException(
                                "❌ 发布认证密钥为空！\n" +
                                        "本地开发：请在local.properties中配置PUBLISH_MAVEN_KEY\n" +
                                        "CI/CD：请通过-Ppublish.key传递GITHUB_TOKEN"
                            )
                        }

                        // 4. 赋值认证信息
                        this.username = username
                        this.password = password
                    }
                }
            }
        }
    }
}

dependencies {
    // Android 核心
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.material)

    // 功能组件 (代码中确实在用的)
    implementation(libs.crashreport)      // Bugly
    implementation(libs.chucker)          // Chucker
    implementation(libs.androidautosize)  // AutoSizeConfig
    implementation(libs.mmkv)             // MMKV
    implementation(libs.gson)             // MMKV 中用于对象转 Json
    api(libs.eventbus)                    // EventBus (保持 api 方便宿主使用)

    // Ktor 网络相关
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // 测试
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}