plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id ("io.realm.kotlin")
}

android {
    namespace = "com.soloscape.util"

    buildFeatures {
        compose = true
    }



}

dependencies {

    implementation (libs.activity.compose)
    implementation (libs.material3.compose)
    implementation (libs.core.ktx)
    implementation (libs.firebase.storage)
    implementation (libs.realm.sync)
    implementation (libs.coroutines.core)
    implementation (libs.coil)
    implementation (libs.compose.tooling.preview)

    //ui
    implementation (projects.core.ui)

}