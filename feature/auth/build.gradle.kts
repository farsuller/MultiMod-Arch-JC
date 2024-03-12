plugins {
    id 'com.android.library'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.soloscape.auth'

    buildFeatures {
        compose true
    }

}

dependencies {

    implementation libs.activity.compose
    implementation libs.material3.compose
    implementation libs.compose.tooling.preview
    implementation libs.navigation.compose
    implementation libs.message.bar.compose
    implementation libs.one.tap.compose
    implementation libs.firebase.auth
    implementation libs.coroutines.core
    implementation libs.realm.sync
    //ui
    implementation project(':core:ui')

    //util
    implementation project(':core:util')
}