plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id("com.google.devtools.ksp")
    id 'dagger.hilt.android.plugin'
    id 'io.realm.kotlin'
    id 'com.google.gms.google-services'
}

def keystorePropertiesFile = rootProject.file("keystore.properties")
def keystoreProperties = new Properties()

if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(new FileInputStream(keystorePropertiesFile))
} else {
    throw new FileNotFoundException("Keystore properties file not found.")
}

android {
    namespace ProjectConfig.namespace

    android.applicationVariants.configureEach { variant ->
        variant.outputs.configureEach {
            archivesBaseName = "${ProjectConfig.appFileName}-${variant.buildType.name}-v${versionCode}-${versionName}"
        }
    }

    signingConfigs {
        debug {
            storeFile file("keystore/debug.keystore")
            storePassword "android"
            keyAlias "AndroidDebugKey"
            keyPassword "android"
        }


        release {
            storeFile file("keystore/soloscape.jks")
            storePassword = keystoreProperties['releaseStorePassword'] as String
            keyAlias = keystoreProperties['releaseKeyAlias'] as String
            keyPassword = keystoreProperties['releaseKeyPassword'] as String
        }
    }

    buildTypes {
        debug{
            applicationIdSuffix ".debug"
            signingConfig signingConfigs.debug
            debuggable true
            minifyEnabled false
        }
        release {
            signingConfig signingConfigs.release
            minifyEnabled false
            debuggable false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    buildFeatures {
        compose true
    }

}

dependencies {
    // Compose Navigation
    implementation libs.navigation.compose

    // Firebase
    implementation libs.firebase.storage

    // Room components
    implementation libs.room.runtime
    ksp libs.room.compiler
    implementation libs.room.ktx

    // Splash API
    implementation libs.splash.api

    // Dagger Hilt
    implementation libs.hilt.android
    ksp libs.hilt.compiler

    // Mongo DB Realm
    implementation libs.realm.sync

    // Desugar JDK
    coreLibraryDesugaring libs.desugar.jdk

    //Leak Canary
    debugImplementation libs.leakcanary.android

    //Profile Installer
    implementation libs.profileinstaller

    implementation project(':core:ui')
    implementation project(':core:util')
    implementation project(':data:mongo')
    implementation project(':feature:auth')
    implementation project(':feature:home')
    implementation project(':feature:note')
}