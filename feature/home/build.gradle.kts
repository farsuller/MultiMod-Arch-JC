plugins {
    id 'com.android.library'
    id 'org.jetbrains.kotlin.android'
    id 'io.realm.kotlin'
    id 'com.google.devtools.ksp'
    id 'dagger.hilt.android.plugin'
}

android {
    namespace 'com.soloscape.home'

    buildFeatures {
        compose true
    }

}

dependencies {
    implementation libs.activity.compose
    implementation libs.material3.compose
    implementation libs.navigation.compose
    implementation libs.coroutines.core
    implementation libs.realm.sync
    implementation libs.hilt.android
    ksp libs.hilt.compiler
    implementation libs.hilt.navigation.compose
    implementation libs.compose.tooling.preview
    implementation libs.date.time.picker
    implementation libs.date.dialog
    implementation libs.firebase.auth
    implementation libs.firebase.storage

    implementation(project(":core:ui"))
    implementation(project(":core:util"))
    implementation(project(":data:mongo"))

}