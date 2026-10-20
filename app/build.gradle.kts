import org.gradle.process.ExecOperations
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import javax.inject.Inject

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "fr.divad.fbxremote"
    compileSdk = 35

    defaultConfig {
        applicationId = "fr.divad.fbxremote"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // Provisoire : signature avec la clé debug pour pouvoir installer la release
            // directement. À remplacer par un vrai keystore avant toute diffusion.
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.06.01")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.1")
    debugImplementation("androidx.compose.ui:ui-tooling")
}

// ─── Sous-ensemble de la police Material Symbols pour la release ───────────────
//
// La police complète (15 Mo) reste dans src/main/res/font : c'est elle que voient
// l'IDE, les aperçus Compose et le build debug. Pour la release, la tâche ci-dessous
// scanne les sources à la recherche des `Sym.xxx` utilisés et produit une police
// réduite portant le même nom de ressource ; placée dans un dossier de ressources
// généré propre à la variante release, elle remplace la police complète.
// Prérequis sur la machine de build : Python 3 et `pip install fonttools`.

abstract class SubsetSymbolsFontTask : DefaultTask() {

    @get:InputFile
    abstract val fullFont: RegularFileProperty

    @get:InputFile
    abstract val codepoints: RegularFileProperty

    @get:InputFiles
    abstract val kotlinSources: ConfigurableFileCollection

    @get:InputFile
    abstract val script: RegularFileProperty

    @get:Input
    abstract val pythonExecutable: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Inject
    abstract val execOps: ExecOperations

    @TaskAction
    fun run() {
        val out = outputDir.get().asFile.resolve("font/${fullFont.get().asFile.name}")
        out.parentFile.mkdirs()
        execOps.exec {
            commandLine(
                pythonExecutable.get(),
                script.get().asFile.absolutePath,
                fullFont.get().asFile.absolutePath,
                codepoints.get().asFile.absolutePath,
                kotlinSources.asPath.split(File.pathSeparator).first(),
                out.absolutePath,
            )
        }
    }
}

val subsetSymbolsFont by tasks.registering(SubsetSymbolsFontTask::class) {
    group = "build"
    description = "Réduit la police Material Symbols aux glyphes utilisés (release uniquement)."
    fullFont.set(layout.projectDirectory.file("src/main/res/font/material_symbols_rounded.ttf"))
    codepoints.set(layout.projectDirectory.file("material_symbols_rounded.codepoints"))
    kotlinSources.from(layout.projectDirectory.dir("src/main/java"))
    script.set(rootProject.layout.projectDirectory.file("tools/subset_symbols.py"))
    pythonExecutable.set(providers.gradleProperty("python.executable").orElse("python"))
    outputDir.set(layout.buildDirectory.dir("generated/symbolsSubset/res"))
}

androidComponents {
    onVariants(selector().withBuildType("release")) { variant ->
        variant.sources.res?.addGeneratedSourceDirectory(subsetSymbolsFont, SubsetSymbolsFontTask::outputDir)
    }
}
