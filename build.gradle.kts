// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.maven.publish) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.gradle)
        classpath(libs.android.maven.gradle.plugin)
    }
}

allprojects {
    extra.apply {
        set("appName", "YaDuoPlayer")
        set("appPackageName", "com.yaduo.yaduoplayer")
        set("versionName", "1.8.0")
        set("versionCode", 10800)
        set("compileSdk", 34)
        set("buildToolsVersion", "34.0.0")
        set("minSdk", 26)
        set("targetSdk", 26)
        set("kotlin_version", "1.6.0")
    }
}