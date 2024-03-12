plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id ("com.google.devtools.ksp")
    id ("io.realm.kotlin")
}

android {
    namespace = "com.soloscape.mongo"

}

dependencies {

    implementation (libs.core.ktx)
    implementation (libs.coroutines.core)
    implementation (libs.realm.sync)

    implementation (libs.room.runtime)
    implementation (libs.room.ktx)
    ksp (libs.room.compiler)

    //util
    implementation(projects.core.util)
}