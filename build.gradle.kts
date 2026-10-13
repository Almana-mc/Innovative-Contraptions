plugins {
    `java-library`
    `maven-publish`
    idea
    id("net.neoforged.moddev")
    id("neoforge-mutex")
}

val modId = property("mod_id") as String
val mcVersion = property("minecraft_version") as String
val javaVersion = (property("java_version") as String).toInt()
val modern = sc.current.parsed >= "26.1"

version = "${property("mod_version")}+$mcVersion"
group = property("mod_group_id") as String

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.blamejared.com/")
}

dependencies {
    val jeiVersion = property("jei_version") as String
    compileOnly("mezz.jei:jei-$mcVersion-neoforge-api:$jeiVersion")
    runtimeOnly("mezz.jei:jei-$mcVersion-neoforge:$jeiVersion")
    testImplementation(platform("org.junit:junit-bom:5.11.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

base {
    archivesName = modId
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = javaVersion
}

neoForge {
    version = property("neo_version") as String
    accessTransformers.from(file("src/main/resources/META-INF/accesstransformer.cfg"))
    validateAccessTransformers = true

    runs {
        create("client") {
            client()
        }

        create("server") {
            server()
            programArgument("--nogui")
        }

        create("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
            if (modern) programArguments.addAll("--tests", (findProperty("tests") ?: "$modId:*").toString())
        }

        create("data") {
            if (modern) clientData() else data()
            programArguments.addAll(
                "--mod", modId,
                "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", rootProject.file("src/main/resources/").absolutePath
            )
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

val generateModMetadata by tasks.registering(ProcessResources::class) {
    val replaceProperties = mapOf(
        "minecraft_version" to project.property("minecraft_version"),
        "minecraft_version_range" to project.property("minecraft_version_range"),
        "neo_version" to project.property("neo_version"),
        "neo_version_range" to project.property("neo_version_range"),
        "jei_version_range" to project.property("jei_version_range"),
        "loader_version_range" to project.property("loader_version_range"),
        "mod_id" to project.property("mod_id"),
        "mod_name" to project.property("mod_name"),
        "mod_license" to project.property("mod_license"),
        "mod_version" to project.property("mod_version"),
        "mod_authors" to project.property("mod_authors"),
        "mod_description" to project.property("mod_description")
    )
    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from(rootProject.file("src/main/templates"))
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}

sourceSets {
    main {
        resources.srcDir(file("src/generated/resources"))
        resources.srcDir(generateModMetadata)
    }
}

neoForge {
    ideSyncTask(generateModMetadata)
}

tasks.named("createMinecraftArtifacts") {
    dependsOn("stonecutterGenerate")
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = layout.projectDirectory.dir("repo").asFile.toURI()
        }
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
