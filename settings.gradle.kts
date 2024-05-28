pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        //네이버 맵을 사용하기 위한 의존성 설치
        maven {
            //url = uri("https://naver.jfrog.io/artifactory/maven/")
            url = uri("https://repository.map.naver.com/archive/maven")
        }

        maven {
            url = uri("https://jitpack.io")
        }
    }
}

rootProject.name = "CityFarmer"
include(":app")
