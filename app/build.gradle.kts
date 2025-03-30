plugins {
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.android.application")
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
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    dependencies {
        // Firebase dependencies (Latest versions)
        implementation(platform("com.google.firebase:firebase-bom:32.8.0")) // Update BOM
        implementation("com.google.firebase:firebase-analytics")
        implementation("com.google.firebase:firebase-auth-ktx:22.3.0") // Latest version

        // AndroidX dependencies (Check if these resolve correctly)
        implementation("androidx.core:core-ktx:1.12.0")
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.11.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

        // Google Maps & Location
        implementation("com.google.android.gms:play-services-maps:18.2.0") // Updated
        implementation("com.google.android.gms:play-services-location:21.2.0") // Updated

        // Networking
        implementation("com.android.volley:volley:1.2.1")
        implementation("org.json:json:20231013") // Newest version
        implementation("com.squareup.okhttp3:okhttp:4.12.0") // Latest version

        // Animation Libraries
        implementation("com.airbnb.android:lottie:6.3.0") // Latest Lottie version

        // Bottom Navigation Bar
        implementation("io.github.zagori:bottomnavbar:1.0.3") // Keep if working fine

        // OSMDroid
        implementation("org.osmdroid:osmdroid-android:6.1.16") // Latest update

        // Testing
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//      picasso for profile
        implementation("com.squareup.picasso:picasso:2.8")
        // for circle image or background glide
        implementation("com.github.bumptech.glide:glide:4.16.0")
        annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

        // loader annimation








    }

}