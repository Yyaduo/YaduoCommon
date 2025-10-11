//import java.io.File
//import java.io.FileWriter
//import java.text.SimpleDateFormat
//import java.util.Date
//import org.gradle.api.Project
//
//
//val rootExtra = rootProject.extra
//
//rootExtra.apply {
//
//    // 执行系统命令并返回命令的输出结果
//    set("executeCmd") { cmd: String ->
//        val process = Runtime.getRuntime().exec(cmd)
//        process.waitFor()
//        process.inputStream.bufferedReader().use { it.readText().trim() }
//    }
//
//    // 获取当前 Git 分支名称
//    set("getBranch") { dir: String ->
//        try {
//            // 更可靠的获取当前分支命令
//            (rootExtra["executeCmd"] as (String) -> String)
//                .invoke("git -C $dir rev-parse --abbrev-ref HEAD")
//        } catch (e: Exception) {
//            "unknown-branch" // 出错时使用默认值
//        }
//    }
//
//    // 获取当前 Git 用户名称
//    set("getUser") { dir: String ->
//        (rootExtra["executeCmd"] as (String) -> String).invoke("git -C $dir config user.name")
//    }
//
//    // 获取打包时间戳，格式可自定义
//    set("getPackTime") { fmt: String? ->
//        val pattern = fmt ?: "yyyyMMddHHmmss"
//        SimpleDateFormat(pattern).format(Date())
//    }
//
//    // 获取构建类型（Debug / Release）同时返回类型和布尔值
//    set("getBuildType") { proj: Project ->
//        // 通过 gradle 任务名判断构建类型
//        val isRelease = proj.gradle.startParameter.taskNames.any { taskName ->
//            taskName.contains("release", ignoreCase = true)
//        }
//        mapOf(
//            "type" to if (isRelease) "Release" else "Debug",
//            "isDebug" to !isRelease
//        )
//    }
//
//    // 生成版本信息文件 ApplicationVersion.kt
//    set("genVersionFile") { proj: Project, pkg: String ->
//        val projPath = proj.projectDir.absolutePath // 获取项目路径
//        val versionDirPath = "${proj.buildDir}/generated/source/version" // 生成文件的目录
//        val fileDirPath = versionDirPath + "/" + pkg.replace(".", File.separator) // 替换包名为目录路径
//        val fileDir = File(fileDirPath)
//        fileDir.mkdirs()
//
//        val versionFile = File(fileDir, "ApplicationVersion.kt")
//        versionFile.createNewFile()
//
//        // 从根项目读取 appName、appPackageName、versionCode 和 versionName
//        val rootProject = proj.rootProject
//        val appName = rootProject.extra["appName"] as String
//        val packageName = rootProject.extra["appPackageName"] as String
//        val versionName = rootProject.extra["versionName"] as String
//        val versionCode = rootProject.extra["versionCode"] as Int
//
//        // 获取构建类型信息
//        val buildTypeInfo = (this["getBuildType"] as (Project) -> Map<String, Any>)(proj)
//        val buildType = buildTypeInfo["type"] as String
//        val isDebug = buildTypeInfo["isDebug"] as Boolean
//
//        val fileContent = """
//        |package $pkg
//        |
//        |/**
//        | * 脚本自动生成，请勿修改
//        | * 当前APK的版本信息
//        | * @author YaDuo
//        | */
//        |object ApplicationVersion {
//        |
//        |    /** 应用名 **/
//        |    const val APP_NAME = "$appName"
//        |
//        |    /** 应用包名 **/
//        |    const val PACKAGE_NAME = "$packageName"
//        |
//        |    /** 应用版本代码 **/
//        |    const val VERSION_CODE = $versionCode
//        |
//        |    /** 应用版本名称 **/
//        |    const val VERSION_NAME = "$versionName"
//        |
//        |    /** 构建类型：Debug 或 Release **/
//        |    const val BUILD_TYPE = "$buildType"
//        |
//        |    /** 提供外部判断是否是Debug版本 **/
//        |    const val DEBUG = $isDebug
//        |
//        |    /** 当前 Git 用户名称 **/
//        |    const val COMPUTER = "${(rootExtra["getUser"] as (String) -> Any)(projPath)}"
//        |
//        |    /** 打包时间戳 **/
//        |    const val PACKTIME = "${(rootExtra["getPackTime"] as (String?) -> Any)(null)}"
//        |
//        |    /** 当前 Git 分支名称 **/
//        |    const val BRANCH = "${(rootExtra["getBranch"] as (String) -> Any)(projPath)}"
//        |
//        |}
//    """.trimMargin()
//
//        FileWriter(versionFile).use { writer ->
//            writer.write(fileContent)
//        }
//    }
//}