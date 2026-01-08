# YaduoCommon

[![Release](https://img.shields.io/github/v/release/Yyaduo/YaduoCommon?include_prereleases)](https://github.com/YaDuo/YaduoCommon/releases)
[![License](https://img.shields.io/badge/License-个人免费_商业需授权-orange)](./LICENSE)
[![Language](https://img.shields.io/badge/language-Kotlin/Java-brightgreen)](https://kotlinlang.org/)
[![Platform](https://img.shields.io/badge/platform-Android-blue)](https://developer.android.com/)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26%20(Android%208.0)-orange)](https://developer.android.com/about/versions/android-8.0)
[![AGP](https://img.shields.io/badge/AGP-8.9.3+-red)](https://developer.android.com/studio/releases/gradle-plugin)
[![JitPack](https://jitpack.io/v/Yyaduo/YaduoCommon.svg)](https://jitpack.io/#Yyaduo/YaduoCommon)

> YaduoCommon 是一套轻量、高效的 Android 通用工具库。
> 封装日常开发高频核心能力，支持模块化初始化、自动适配业务场景，大幅降低重复开发成本。

## 🗺 目录

- [📂项目结构](#-项目结构)
- [📱兼容性](#-兼容性)
- [🚀核心功能模块](#-核心功能模块)
- [🧩模块化初始化设计](#-模块化初始化设计)
- [🛠集成方式](#-集成方式)
- [💡快速使用示例](#-快速使用示例)
    - [屏幕适配（AutoSizeConfig）](#屏幕适配autosizeconfig)
    - [Bugly 崩溃上报（BuglyReport）](#bugly-崩溃上报buglyreport)
    - [MMKV 数据存储](#mmkv-数据存储)
    - [网络监控（Chucker）](#网络监控chucker)
    - [DeviceInfo 设备信息](#deviceinfo-设备信息)
    - [工具类](#工具类)
- [📦自动发布流程](#-自动发布流程)
- [📋版本规范](#-版本规范)
- [⚠️注意事项](#-注意事项)
- [🤝贡献说明](#-贡献说明)
- [📄许可证](#-许可证)
- [✨作者](#-作者)

## 📂 项目结构

| 模块名     | 作用            |
|---------|---------------|
| :app    | 示例工程（演示工具库用法） |
| :common | 核心工具库（实际集成用）  |

## 📱 兼容性

| 类型          | 支持范围                      |
|-------------|---------------------------|
| 最低Android版本 | Android 8.0 (API 26)      |
| 编译Android版本 | Android 14 (API 34)       |
| Gradle版本    | Gradle 8.0+ / AGP 8.9.3+  |
| Kotlin版本    | 2.3.0+                    |
| 架构支持        | 单模块/模块化项目、Jetpack Compose |

## 🚀 核心功能模块

| 模块名称           | 核心能力                                                           |
|----------------|----------------------------------------------------------------|
| AppLogicUtil   | 全局Application管理、模块化初始化（自动检测/手动注册）、Chucker拦截器获取、应用版本代码获取        |
| AutoSizeConfig | 基于AndroidAutoSize的屏幕适配，自动检测Manifest设计稿配置、适配前后回调监听、手动启用/禁用适配    |
| BuglyReport    | 腾讯Bugly崩溃监控封装，自动读取Manifest的BUGLY_APPID、设备UUID/UID自动上报、用户策略配置   |
| Chucker        | 网络请求监控拦截器，敏感头（Authorization/Cookie）脱敏、开发/生产环境隔离、空操作默认拦截器       |
| MMKV           | 腾讯MMKV键值存储封装，支持多类型基础数据、泛型对象JSON序列化、数据管理（contains/remove/clear） |
| DeviceInfo     | 设备UUID/UID获取、网络状态检测、屏幕方向判断（宽高比）、设备信息一键打印                       |
| 工具类            | 提供各种工具类封装，详情查阅com.yaduo.common.util                            |
| 扩展函数           | ProcessLifecycleOwner空安全获取、Context快速显示Toast                    |

## 🧩 模块化初始化设计

### 核心接口：ICommonModule

> 所有功能模块均实现该接口，统一初始化规范：

| 接口方法                    | 作用                              |
|-------------------------|---------------------------------|
| isInitialized           | 标记模块是否已完成初始化                    |
| isCanInitialized        | 标记模块是否满足初始化条件（如Manifest配置是否存在）  |
| initialize(context)     | 模块核心初始化逻辑（默认使用全局Application上下文） |
| checkCanBeInitialized() | 检测是否满足初始化条件，满足则自动初始化            |

## 🛠 集成方式

### 1. 依赖配置

#### JitPack依赖示例：

> 以下 "release_tag" 为该仓库release发布的tag名，如：release-v1.0.0

``` gradle
// Groovy
// settings.gradle
dependencyResolutionManagement {
    repositories {
        // 新增JitPack仓库
        maven { url 'https://jitpack.io' }
        google()
        mavenCentral()
    }
}
// 模块级 build.gradle
dependencies {
    implementation 'com.github.Yyaduo:YaduoCommon:${release_tag}'
}
```

``` kotlin
// Kotlin DSL
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        // 新增JitPack仓库
        maven("https://jitpack.io")
        google()
        mavenCentral()
    }
}
// 模块级 build.gradle.kts
dependencies {
    implementation(libs.yaduo.common)
}
```

``` toml
# toml
[libraries]
yaduo-common = { module = "com.github.Yyaduo:YaduoCommon", version.ref = "yaduocommon" }

[versions]
yaduocommon = "${release_tag}"
```

#### GitHub Packages依赖示例:

> ⚠️ 注意：GitHub Packages 需使用 Personal Access Token (PAT) 认证。
> 禁止硬编码 Token 到代码中，建议通过环境变量 /gradle.properties 配置
>
PAT创建步骤：https://docs.github.com/cn/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token
> PAT需勾选权限：`read:packages`
> 以下 "GitHub_Packages_Version" 为该仓库GitHubPackages的版本号，如：1.0.0

``` gradle
// Groovy
// settings.gradle
dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/Yyaduo/YaduoCommon")
            credentials {
                username = System.getenv("GITHUB_USERNAME") ?: "你的GitHub用户名"
                password = System.getenv("GITHUB_TOKEN") ?: "你的GitHub PAT（需有read:packages权限）"
            }
        }
    }
}
// 模块级 build.gradle
dependencies {
    implementation 'com.github.Yyaduo.common:${GitHub_Packages_Version}'
}
```

``` kotlin
// Kotlin DSL
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/Yyaduo/YaduoCommon")
            credentials {
                username = System.getenv("GITHUB_USERNAME") ?: "你的GitHub用户名"
                password = System.getenv("GITHUB_TOKEN") ?: "你的GitHub PAT（需有read:packages权限）"
            }
        }
    }
}
// 模块级 build.gradle.kts
dependencies {
    implementation(libs.yaduo.common)
}
```

``` toml
# toml
[libraries]
yaduo-common = { module = "com.github.Yyaduo:common", version.ref = "yaduocommon" }

[versions]
yaduocommon = "${GitHub_Packages_Version}"
```

### 2. 初始化（必须）

在 Application 的 onCreate() 中执行初始化：

``` kotlin
class MyApplication : Application() {
   override fun onCreate() {
   super.onCreate()
   // 推荐：自动检查模块配置 + 初始化符合条件的模块
   // needCheck=true：自动检测所有模块的初始化条件
   // needInitialize=false：仅检测，满足条件则自动初始化
   AppLogicUtil.initialize(app = this)

   // 备选：手动注册并初始化所有模块
   // AppLogicUtil.initializeAllCommonModule()
   
   // 备选：注册单个模块并手动初始化
   AppLogicUtil.initialize(app = this, needCheck = false)
   // 注册单个模块
   AppLogicUtil.registerCommonModule(MMKV)
   AppLogicUtil.registerCommonModule(LogUtil)
   // 初始化已注册的模块
   AppLogicUtil.initializeCommonModule()
   }
}
```

## 💡 快速使用示例

### 屏幕适配（AutoSizeConfig）

``` xml
<!-- 步骤1：在AndroidManifest.xml添加设计稿尺寸 -->
<application>
    <meta-data
        android:name="design_width_in_dp"
        android:value="360"/>
    <meta-data
        android:name="design_height_in_dp"
        android:value="640"/>
</application>
```

``` kotlin
// 自动检测Manifest配置并启用适配
AutoSizeConfig.checkCanBeInitialized(applicationContext)
// 手动禁用适配（公开方法）
AutoSizeConfig.disableAutoSizeAdapt()
```

### Bugly 崩溃上报（BuglyReport）

``` xml
<!-- 在AndroidManifest.xml配置AppID -->
<application>
    <meta-data
        android:name="BUGLY_APPID"
        android:value="你的Bugly AppID"/> <!-- 从Bugly官网控制台获取 -->
</application>
```

``` kotlin
// 初始化后自动上报，登录后更新用户ID
com.tencent.bugly.crashreport.CrashReport.setUserId("user123")
```

### MMKV 数据存储

``` kotlin
// 基础类型存储
MMKV.putString("user_name", "YaDuo")
val userName = MMKV.getString("user_name", "默认值")

// 对象存储
data class User(val id: Int, val name: String)
MMKV.putObjectAsJsonObject("user_info", User(1, "YaDuo"))
val user = MMKV.getObjectFromJsonObject("user_info", User::class.java)

// 多进程安全存储
val multiProcessMMKV = MMKV.mmkvWithID("multi_process_db", MMKV.MULTI_PROCESS_MODE)
multiProcessMMKV.putInt("process_count", 1)
```

### 网络监控（Chucker）

``` kotlin
// 集成到OkHttp
/**
 * 拦截器特性：
 * - 未初始化Chucker时，返回空操作拦截器（不影响网络请求）
 * - 自动脱敏敏感头：Authorization、Cookie
 * - 限制捕获数据大小：512KB
 */
val okHttpClient = OkHttpClient.Builder()
.addInterceptor(AppLogicUtil.getChuckerInterceptor())
.build()
```

### DeviceInfo 设备信息

``` kotlin
// 一键打印所有设备信息
DeviceInfo.printDeviceInfo()

// 获取单个信息
val uuid = DeviceInfo.getUuid() // 设备序列号（Build.SERIAL）
val uid = DeviceInfo.getUid() // 基于UUID生成的唯一UID
val isNetworkConnected = DeviceInfo.isNetworkConnected() // 网络是否连接
val isPortrait = DeviceInfo.isVisualPortrait() // 是否竖屏（宽≤高）
```

### 工具类
> 封装增强日志打印、Manifest MetaData 读取、权限申请等高频开发能力，
> 包含 LogUtil、MetaDataUtils、PermissionUtils 等工具类，具体用法请查阅 com.yaduo.common.util 下的代码实现。

## 📦 自动发布流程

### 本项目配置 GitHub Actions 工作流，触发规则如下：

1. 推送代码时，若包含符合语义化版本的 Tag（如 v1.0.0），自动创建 GitHub Release
2. Release 自动包含：
    - 编译后的 aar 包
    - 源码压缩包
    - 基于 Commit 记录生成的更新日志

### 本地手动发布到 GitHub Packages

- 配置认证信息：

``` properties
# 本地开发：在 `local.properties` 中添加：
PUBLISH_MAVEN_USER=你的GitHub用户名
PUBLISH_MAVEN_KEY=你的GitHub PAT（需有read:packages/write:packages权限）
VERSION_NAME=1.0.0 # 发布版本号
```

- 执行发布命令:

``` bash
# 发布到GitHub Packages（开启校验）
./gradlew :common:publish -PpublishToGitHubPackages=true -PVERSION_NAME=1.0.0

# 跳过校验（仅本地测试，不发布到GitHub Packages）
./gradlew :common:publish -PskipPublishCheck=true -PVERSION_NAME=1.0.0
```

## 📋 版本规范

### 遵循 [Semantic Versioning 2.0](https://semver.org/lang/zh-CN/) 语义化版本：

- **主版本号**：不兼容的 API 变更（例：1.x → 2.x）
- **次版本号**：向后兼容的功能新增（例：1.0 → 1.1）
- **修订号**：向后兼容的问题修复（例：1.1.0 → 1.1.1）

## ⚠️ 注意事项

1. 版本兼容：
    - 最低兼容版本：Android 8.0 (API 26)
    - AGP 版本要求：8.9.3+
    - Kotlin 版本要求：2.3.0+
    - 编译SDK版本：34（Android 14）
2. 依赖库说明：
    - **AndroidAutoSize**：v1.2.1（屏幕适配核心依赖）
    - **Bugly SDK**：latest.release（崩溃监控，需自行确认具体版本）
    - **Chucker**：4.2.0（仅开发环境生效，生产环境自动禁用）
    - **MMKV**：mmkv-static 2.3.0（已内置封装，无需额外依赖）
    - **EventBus**：3.3.1（以api方式暴露，宿主可直接使用）
    - **Ktor**：3.3.3（网络请求核心依赖，包含okhttp引擎、JSON序列化）
    - **Gson**：2.13.2（MMKV对象序列化依赖）
    - **Lifecycle-process**：2.10.0（应用生命周期监听）
3. 功能限制：
    - LogUtil 的调用栈信息（文件名/行号）可能不准确（代码内 FIXME 标记）；
    - MMKV 存储泛型对象时，目标类需有默认构造函数，否则Gson反序列化失败；
    - Chucker 未初始化时，getChuckerInterceptor() 返回空操作拦截器，不影响网络请求；
    - DeviceInfo.isNetworkConnected() 在 Android 10+ 需申请 ACCESS_NETWORK_STATE 权限；
4. 性能提示：
    - MMKV 不建议存储超过100KB的大量数据，大数据推荐使用数据库；
    - Bugly 初始化延迟3000ms（代码内用户策略配置），避免影响应用启动性能；
5. 安全提示：
    - Chucker 自动脱敏 Authorization、Cookie 等敏感头，保护用户隐私；
    - 生产环境建议关闭 LogUtil 的栈信息打印（代码内 isNeedPrintStack 控制）；
6. 必要权限：

``` xml
<!-- 网络状态获取 -->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<!-- 网络请求 -->
<uses-permission android:name="android.permission.INTERNET"/>
```

## 🤝 贡献说明

本项目遵循「允许使用 / fork，修改需授权」的规则：

1. 可自由 fork 仓库、使用完整未修改版本；
2. 若需修改代码/文档，需先通过以下方式联系作者获取书面授权：
    - GitHub Issue：https://github.com/Yyaduo/YaduoCommon/issues
    - 邮箱：2499133184@qq.com；
3. 贡献代码需符合以下规范：
    - 提交PR前确保代码无编译错误、无冗余日志；
    - Commit信息格式：`CommitTag: 此次release_tag -> [模块名 / 功能 / 类名]具体描述`
      **（例：feat: release-v1.1.0 -> [MMKV] 接入MMKV）**；
    - 新增功能需补充对应的使用示例和注释；
    - 遵循项目编码规范（Kotlin官方编码规范）。

## 📄 许可证

> 本项目基于 **YaduoCommon 自定义授权许可证** 发布，核心规则如下：

| 使用场景             | 权限说明             | 是否需要授权  |
|------------------|------------------|---------|
| 个人非商业使用          | 自由使用、修改（仅限自用）    | ❌ 无需授权  |
| 二次分发（完整未修改代码）    | 允许分发，仅限他人个人非商业使用 | ❌ 无需授权  |
| 商业使用（企业/付费/产品嵌入） | 禁止未经授权使用         | ✅ 需书面授权 |
| 修改后二次分发          | 禁止未经授权分发         | ✅ 需书面授权 |

完整许可证内容请查阅 [LICENSE](./LICENSE) 文件。

### 授权申请方式

如需商业授权或修改后分发授权，请通过以下途径联系作者：

- GitHub Issues: https://github.com/YaDuo/YaduoCommon/issues
- 邮箱: 2499133184@qq.com

## ✨ 作者

- **YaDuo**
- **维护周期**：2025 - 至今