plugins {
    alias(libs.plugins.android.application)
}

android {
<<<<<<< HEAD
    namespace = "edu.uph.m24si2.studypets"
    compileSdk = 34

    defaultConfig {
        applicationId = "edu.uph.m24si2.studypets"
        minSdk = 30
        targetSdk = 34
=======
    namespace = "edu.uph.m24si2.studypet"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "edu.uph.m24si2.studypet"
        minSdk = 30
        targetSdk = 36
>>>>>>> c0e7c78c803140c3c029b01320da97d86db9b09b
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
<<<<<<< HEAD
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
=======
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
>>>>>>> c0e7c78c803140c3c029b01320da97d86db9b09b
    }
}

dependencies {
<<<<<<< HEAD
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.vectordrawable.animated)

    // Room — database lokal Android (pengganti Realm)
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
=======
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}
>>>>>>> c0e7c78c803140c3c029b01320da97d86db9b09b
