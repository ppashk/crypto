buildscript {
    repositories { mavenCentral() }
    dependencies { classpath 'org.yaml:snakeyaml:1.27' }
}

group 'com.crypto'

plugins.withId('java') {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
    compileJava {
        options.encoding = 'UTF-8'
        options.compilerArgs << '-Xlint:unchecked' << '-Xlint:deprecation' << '-Werror'
    }
    compileTestJava {
        options.encoding = 'UTF-8'
        options.compilerArgs << '-Xlint:unchecked' << '-Xlint:deprecation' << '-Werror'
    }
    configurations {
        compile.exclude module: 'spring-boot-starter-tomcat'
    }
    tasks.withType(AbstractArchiveTask) {
        preserveFileTimestamps = false
        reproducibleFileOrder = true
    }
    test {
        maxHeapSize = '1G'
        useJUnitPlatform()
    }

    task resolveDependencies {
        doLast {
            rootProject.allprojects { project ->
                Set<Configuration> configurations = project.buildscript.configurations + project.configurations
                configurations.findAll { c -> c.canBeResolved }
                        .forEach { c -> c.resolve() }
            }
        }
    }
}
plugins.withId('java-library') {
    tasks.jar.dependsOn tasks.check
    //configurations.all {
    //    resolutionStrategy {
    //        // fail eagerly on version conflict (includes transitive dependencies)
    //        // e.g. multiple different versions of the same dependency (group and name are equal)
    //        failOnVersionConflict()
    //    }
    //}
}
plugins.withId('nebula.lint') {
    gradleLint {
        rules = ['all-dependency']
    }
}
plugins.withId('com.autonomousapps.dependency-analysis') {

}
plugins.withId('ca.cutterslade.analyze') {

}
plugins.withId('io.spring.dependency-management') {
    ext['scala.version'] = '2.12'
    ext['scala-full.version'] = '2.12.12'
    ext['spark.version'] = '3.1.1'
    ext['log4j2.version'] = '2.16.0'
    // gcr.io/spark-operator/spark:v3.1.1 is build with hadoop 2.7
    // to check hadoop 2.7 version: https://github.com/apache/spark/blob/v3.1.1/pom.xml#L3157
    dependencyManagement {
        imports {
            // spring-boot-dependencies should be declared last to override everything on top
            mavenBom 'org.springframework.boot:spring-boot-dependencies:2.4.4'
        }
        dependencies {
            dependency 'org.springframework.boot:spring-boot-starter:2.4.4'
            dependency 'com.google.guava:guava:30.1.1-jre'
            dependency 'org.codehaus.janino:janino:3.0.10'
            dependency 'net.logstash.logback:logstash-logback-encoder:6.6'
            dependency 'org.projectlombok:lombok:1.18.22'
            dependency 'com.nimbusds:nimbus-jose-jwt:9.7'
            dependency 'com.github.spotbugs:spotbugs-annotations:4.2.2'
            dependency 'com.github.RohitA5L:embedded-redis:90f9bfabf4'
        }
    }
}
plugins.withId('org.springframework.boot') {
    tasks.withType(Exec) {
        // Workaround to run task from IDEA
        if (!System.getProperty('os.name').toLowerCase().contains('windows')) {
            environment PATH: "/usr/local/sbin:/usr/local/bin:${System.getenv('PATH')}"
        }
    }
}
plugins.withId('org.jetbrains.gradle.plugin.idea-ext') {
    idea {
        project {
            settings {
                delegateActions {
                    delegateBuildRunToGradle = false
                    testRunner = 'PLATFORM'
                }
            }
        }
    }
}
plugins.withId('pmd') {
    pmd {
        toolVersion = '6.33.0'
        ignoreFailures = false
        consoleOutput = false
    }
    tasks.withType(Pmd) {
        ruleSetFiles = files("../etc/java-support/lint/pmd/ruleSetMain.xml")
        ruleSets = []
        exclude '**/api/**'
    }
}
plugins.withId('checkstyle') {
    checkstyle {
        toolVersion = '8.41.1'
        ignoreFailures = false
        showViolations = false
    }
    tasks.withType(Checkstyle) {
        configFile = file("../etc/java-support/lint/checkstyle/checkstyleMain.xml")
        exclude '**/api/**'
    }
    dependencies {
        checkstyle 'com.thomasjensen.checkstyle.addons:checkstyle-addons:6.0.1'
        checkstyle 'com.github.sevntu-checkstyle:sevntu-checks:1.38.0'
    }

}
plugins.withId('com.github.spotbugs') {
    spotbugs {
        toolVersion = '4.2.2'
        ignoreFailures = false
        effort = 'max'
        reportLevel = 'low'
    }
    afterEvaluate {
        tasks.each { task ->
            if (task.class.toString().toLowerCase().contains('spotbugs')) {
                task.excludeFilter = file("../etc/java-support/lint/findbugs/excludeMain.xml")
                task.reports {
                    xml.enabled = false
                    html.enabled = true
                }
            }
        }
    }
}

