plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.soloscape.auth"

    buildFeatures {
        compose = true
    }

}

dependencies {
    implementation (libs.activity.compose)
    implementation (libs.material3.compose)
    implementation (libs.compose.tooling.preview)
    implementation (libs.navigation.compose)
    implementation (libs.message.bar.compose)
    implementation (libs.one.tap.compose)
    implementation (libs.firebase.auth)
    implementation (libs.coroutines.core)
    implementation (libs.realm.sync)

    implementation(projects.core.ui)
    implementation(projects.core.util)
}