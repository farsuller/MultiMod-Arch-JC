plugins {
    id 'com.android.library'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.soloscape.ui'

    buildFeatures{
        compose true
    }

}

dependencies {

    implementation libs.activity.compose
    implementation libs.material3.compose
    implementation libs.compose.tooling.preview
    implementation libs.realm.sync
    implementation libs.coroutines.core
    implementation libs.material.icons.extended
}