plugins {
    alias(libs.plugins.android.application)
    // AJOUT 1 : Le plugin Google Services pour Firebase
    id("com.google.gms.google-services")
}

android {
    // Vérifie que c'est bien ton nom de package ici
    namespace = "com.example.freelance"

    // 👇 CORRECTION : On passe de 34 à 36
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.freelance"
        minSdk = 24

        // 👇 CORRECTION : On passe de 34 à 36
        targetSdk = 36

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    // Authentification Firebase
    implementation("com.google.firebase:firebase-auth")
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // AJOUT 2 : Les dépendances Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-firestore")
}