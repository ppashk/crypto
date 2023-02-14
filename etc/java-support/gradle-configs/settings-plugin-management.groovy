def pluginVersions = [
        'org.springframework.boot'                   : '2.4.4',
        'io.spring.dependency-management'            : '1.0.11.RELEASE',
        'com.github.spotbugs'                        : '4.7.0',
        'com.github.slamdev.openapi-spring-generator': '0.1.6',
        'org.jetbrains.gradle.plugin.idea-ext'       : '1.0',
        'nebula.lint'                                : '16.17.1',
        'com.autonomousapps.dependency-analysis'     : '0.71.0',
        'ca.cutterslade.analyze'                     : '1.5.2',
]
resolutionStrategy { strategy ->
    strategy.eachPlugin { resolver ->
        String version = pluginVersions.get(resolver.requested.id.id)
        if (version) {
            resolver.useVersion(version)
        }
    }
}
repositories {
    gradlePluginPortal()
}
