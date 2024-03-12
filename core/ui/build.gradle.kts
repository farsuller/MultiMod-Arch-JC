plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.soloscape.ui"
    buildFeatures{
        compose = true
    }
}

dependencies {
    implementation (libs.activity.compose)
    implementation (libs.material3.compose)
    implementation (libs.compose.tooling.preview)
    implementation (libs.realm.sync)
    implementation (libs.coroutines.core)
    implementation (libs.material.icons.extended)
}