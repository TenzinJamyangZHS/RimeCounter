plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp") version "2.0.10-1.0.24"
    id("kotlin-kapt")
}

android {
    namespace = "com.rimetech.rimecounter"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rimetech.rimecounter"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures{
        dataBinding = true
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildToolsVersion = "35.0.0"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

dependencies {
    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.room:room-rxjava2:$room_version")
    implementation("androidx.room:room-rxjava3:$room_version")
    implementation("androidx.room:room-guava:$room_version")
    testImplementation("androidx.room:room-testing:$room_version")
    implementation("androidx.room:room-paging:$room_version")
    implementation ("androidx.appcompat:appcompat:1.7.0")
}

dependencies {


    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation ("com.github.Dimezis:BlurView:version-2.0.5")
    implementation ("com.google.code.gson:gson:2.11.0")
    implementation ("com.quiph.ui:recyclerviewfastscroller:1.0.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("androidx.compose.material:material:1.7.5")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.compose.material3:material3-window-size-class:1.3.1")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.4.0-alpha04")

    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    implementation ("androidx.security:security-crypto:1.1.0-alpha06")


}