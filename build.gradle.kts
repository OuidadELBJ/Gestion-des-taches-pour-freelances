plugins {
    alias(libs.plugins.android.application) apply false
    // J'ai enlevé la ligne "library" ici

    id("com.google.gms.google-services") version "4.4.0" apply false
}