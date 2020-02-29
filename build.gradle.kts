plugins {
    java
}

group = "kr.heartpattern.spikot"
version = "1.0.0-SNAPSHOT"

repositories {
    maven("https://maven.heartpattern.kr/repository/maven-public/")
}

dependencies {
    compile("org.apache.maven.resolver:maven-resolver-api:1.4.1")
    compile("org.apache.maven.resolver:maven-resolver-spi:1.4.1")
    compile("org.apache.maven.resolver:maven-resolver-util:1.4.1")
    compile("org.apache.maven.resolver:maven-resolver-impl:1.4.1")
    compile("org.apache.maven.resolver:maven-resolver-transport-file:1.4.1")
    compile("org.apache.maven.resolver:maven-resolver-connector-basic:1.4.1")
    compile("org.apache.maven.resolver:maven-resolver-transport-http:1.4.1")
    compile("org.apache.maven:maven-resolver-provider:3.5.0")
    compile("com.google.code.gson:gson:2.8.6")

    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:plugin-annotations:1.2.2-SNAPSHOT")
    testCompile("junit", "junit", "4.12")

    annotationProcessor("org.spigotmc:plugin-annotations:1.2.2-SNAPSHOT")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks{
    create<Jar>("createPlugin"){
        from(
            configurations["compile"].map{
                if(it.isDirectory)
                    it
                else
                    zipTree(it)
            }
        )

        with(jar.get())
    }
}