plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.ui.rakshakawatch"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ui.rakshakawatch"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        android.buildFeatures.buildConfig = true
        buildConfigField("String", "API_KEY", "\"${System.getenv("API_KEY") ?: project.findProperty("API_KEY")}\"")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Firebase dependencies
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.1")
    implementation("com.google.firebase:firebase-database-ktx:20.3.0")

    // AndroidX dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.preference.ktx)

    // Network & JSON Handling
    implementation("com.android.volley:volley:1.2.1")
    implementation("org.json:json:20210307")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    // Animation Libraries
    implementation("com.airbnb.android:lottie:3.4.0")
    implementation("androidx.interpolator:interpolator:1.0.0")

    // googleMap for Maps
//    implementation("com.mapbox.maps:android:10.13.0")
    implementation("com.google.android.gms:play-services-maps:18.0.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")


    // OSMDroid (If you are still using this, otherwise remove)
    implementation("org.osmdroid:osmdroid-android:6.1.11")

    // Testing Dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //bottom bar with floating action
    implementation( "io.github.zagori:bottomnavbar:1.0.3")

}
